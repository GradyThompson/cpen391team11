module bitstream_tb();
	reg clk, reset_n, en, setaddr;
	wire rdy, full;
	reg [15:0] in_bits;
	reg [7:0] i;
	reg [3:0] in_len;
	wire [31:0] address;
	reg  [31:0] abase;
	wire write;
	wire [15:0] wdata;
	
	bitstream DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en),
	.full(full), .setaddr(setaddr), .in_bits(in_bits), .in_len(in_len),
	.abase(abase), .aend(32'hFFFFFFFF), .address(address), .write(write),
	.waitrequest(1'b0), .writedata(wdata));
	
	initial begin
		for (i = 8'b0; i < 8'd255; i = i + 8'd1) begin
			clk = 1'b0;
			#5;
			clk = 1'b1;
			#5;
		end
	end

	initial begin
		// Reset sequence
		reset_n = 1'b0;
		@(posedge clk);
		@(posedge clk);
		reset_n = 1'b1;
		@(posedge clk);
		if (rdy == 1'b0) begin
			@(posedge rdy);
		end
		setaddr = 1'b1;
		abase = 32'h00010000;
		@(posedge clk);
		abase = 32'h00000000;
		setaddr = 1'b0;
		

		if (rdy == 1'b0) begin
			@(posedge rdy);
		end
		in_bits = 16'h123;
		in_len  = 4'd12;
		en = 1'b1;
		@(posedge clk);
		en = 1'b0;

		if (rdy == 1'b0) begin
			@(posedge rdy);
		end
		in_bits = 16'h4;
		in_len  = 4'd4;
		en = 1'b1;
		@(posedge clk);
		en = 1'b0;

		@(posedge write);		
		if (wdata !== 16'h1234) begin
			$error("0x123 and 0x4 form %x", wdata);
		end else if (address !== 32'h00010000) begin
			$error("Address %x != 0x10000", address);
		end else begin
			$display("Test case 1 complete");
		end

		if (rdy == 1'b0) begin
			@(posedge rdy);
		end
		in_bits = 16'hACF;
		in_len  = 4'd13;
		en = 1'b1;
		@(posedge clk);
		en = 1'b0;

		if (rdy == 1'b0) begin
			@(posedge rdy);
		end
		in_bits = 16'h0;
		in_len  = 4'd3;
		en = 1'b1;
		@(posedge clk);
		en = 1'b0;

		@(posedge write);		
		if (wdata !== 16'h5678) begin
			$error("0xACF and 0x0 form %x", wdata);
		end else if (address !== 32'h00010002) begin
			$error("Address %x != 0x10002", address);
		end else begin
			$display("Test case 2 complete");
		end
	end
endmodule
