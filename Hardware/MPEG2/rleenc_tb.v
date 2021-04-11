module rleenc_tb();
	reg clk, rst, en, hrdy, done;
	wire rdy, hen, hend, dc;
	wire [5:0] addr;
	reg [15:0] q;
	// Interface to hash table
	wire [15:0] val, dcval;
	wire [5:0]  len;
	reg [7:0] i;
	reg [1:0] counter;

	reg [15:0] inRAM [63:0];
	reg [15:0] testRAM [63:0];
	reg [15:0] solnRAM [63:0];

	rleenc DUT(.clk(clk), .reset_n(rst), .rdy(rdy), .en(en), .addr(addr), .q(q),
	.h_rdy(hrdy), .h_en(hen), .h_val(val), .h_len(len), .h_end(hend), .h_dc(dc));

	register #(16) DC(.clk(clk), .in(rst ? val : 16'b0), .out(dcval), .en(dc));
	
	task assertmem;
	begin
		for (i = 8'd0; i < 8'd64; i = i + 8'd1) begin
			if (testRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0d == %0d", $time, testRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0d != %0d", $time, testRAM[i], solnRAM[i]);
			end
		end
	end
	endtask
	
	always @(posedge clk) begin
		if (hen) begin
			hrdy = 1'b0;
			counter = 6'b0;
			if (hend) begin
				done = 1'b1;
			end else if (dc == 1'b0) begin
				testRAM[i] = val;
				testRAM[i + 1] = {10'b0, len};
				i = i + 16'd2;
			end
		end else if (!hrdy) begin
			counter = counter + 2'd1;
			if (counter == 2'd0) begin
				hrdy = 1'b1;
			end
		end
		q = inRAM[addr];
	end

	initial begin
		#10;
		$readmemh("dct2d.soln.memh", inRAM);
		for (i = 8'd0; i < 8'd64; i = i + 8'd1) begin
			solnRAM[i] = 16'd0;
			testRAM[i] = 16'd0;
		end
		solnRAM[6'd0] = -16'd1;
		solnRAM[6'd2] = -16'd3;
		solnRAM[6'd3] = 16'd1;
		solnRAM[6'd4] = -16'd1;
		solnRAM[6'd6] = -16'd5;
		solnRAM[6'd8] = 16'd2;
		solnRAM[6'd10] = -16'd3;
		solnRAM[6'd12] = -16'd2;
		solnRAM[6'd13] = 16'd1;
		solnRAM[6'd14] = 16'd4;
		solnRAM[6'd15] = 16'd2;
		solnRAM[6'd16] = 16'd2;
		solnRAM[6'd17] = 16'd1;
		solnRAM[6'd18] = 16'd2;
		solnRAM[6'd19] = 16'd3;
		solnRAM[6'd20] = -16'd1;
		solnRAM[6'd21] = 16'd1;
		clk = 1'b0;
		rst = 1'b0;
		en = 1'b0;
		hrdy = 1'b0;
		counter = 6'b0;
		i = 8'b0;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		rst = 1'b1;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		hrdy = 1'b0;
		if (!rdy) begin
			$error("Not ready at time %0t!", $time);
		end
		en = 1'b1;
		#5;
		clk = 1'b1;
		#5;
		en = 1'b0;
		#5;
		clk = 1'b0;
		#5;
		while (!rdy) begin
			clk = 1'b1;
			#5;
			clk = 1'b0;
			#5;
		end
		#5;
		assertmem();
		if (dcval === -16'd25) begin
			$display("At time %0t, %0d == %0d", $time, dcval, -16'd25);
		end else begin
			$error("At time %0t, %0d != %0d", $time, dcval, -16'd25);
		end
		if (done === 1'b1) begin
			$display("At time %0t, done is asserted", $time);
		end else begin
			$error("At time %0t, done is not asserted", $time);
		end
	end
endmodule
