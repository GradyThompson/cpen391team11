module dct1d_tb();
	wire [5:0] addr;      // RAM address bus
	wire wren;            // RAM write enable
	wire [15:0] data;     // RAM write data bus
	wire ready;           // Ready signal
	reg clk, reset_n, en; // Enable signal
	reg [15:0] q;         // RAM read data bus
	reg [15:0] testRAM [63:0];
	reg [15:0] solnRAM [63:0];
	reg [6:0] i;

	task assertmem;
	begin
		for (i = 7'd0; i < 7'd64; i = i + 7'd1) begin
			if (testRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0x == %0x", $time, testRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0x != %0x", $time, testRAM[i], solnRAM[i]);
			end
		end
	end
	endtask
	
	dct1d DCT1D(.clk(clk), .reset_n(reset_n), .addr(addr), .wren(wren), .data(data), .rdy(ready), .en(en), .q(q), .rstart(6'h0), .wstart(6'h0), .stride(6'h1));

	always @(*) begin
		if (wren) begin
			q = 16'hxxxx;
		end else begin
			q = testRAM[addr];
		end
	end

	always @(posedge clk) begin
		if (wren) begin
			testRAM[addr] = data;
		end
	end
	
	initial begin
		#10;
		$readmemh("test1.memh", testRAM);
		$readmemh("test1.soln.memh", solnRAM);
		reset_n = 1'b0;
		clk = 1'b0;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		reset_n = 1'b1;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		if (!ready) begin
			$error("Not ready at time %0t!", $time);
		end
		#5;
		en = 1'b1;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		#5;
		while (!ready) begin
			clk = 1'b1;
			#5;
			clk = 1'b0;
			#5;
		end
		#5;
		assertmem();
	end
endmodule
