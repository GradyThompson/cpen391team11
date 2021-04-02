`timescale 1 ns / 1 ns
module idct2d_tb();
	reg clk, reset_n, en;
	wire rdy, wwren;
	reg  iwren;
	reg  [5:0] iaddr, maddr;
	wire [5:0] waddr;
	reg  [7:0] idata;
	reg  [15:0] mq;
	wire [15:0] wdata;

	reg [7:0]  inRAM   [63:0];
	reg [15:0] matrix  [63:0];
	reg [15:0] testRAM [63:0];
	reg [15:0] solnRAM [63:0];
	reg [6:0] i;

	task assertmem;
	begin
		for (i = 7'd0; i < 7'd64; i = i + 7'd1) begin
			if (testRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0d == %0d", $time, testRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0d != %0d", $time, testRAM[i], solnRAM[i]);
			end
		end
	end
	endtask

	idct2d DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en), .iaddr(iaddr), .idata(idata), .iwren(iwren), .mq(mq), .waddr(waddr), .wdata(wdata), .wwren(wwren));

	always @(posedge clk) begin
		if (wwren) begin
			testRAM[waddr] = wdata;
		end
	end

	initial begin
		#10;
		$readmemh("idct2d.in.memh", inRAM);
		$readmemh("imatrix.memh", matrix);
		$readmemh("idct2d.soln.memh", solnRAM);
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
		if (!rdy) begin
			$error("Not ready at time %0t!", $time);
		end
		#5;
		clk = 1'b1;
		#5;
		clk = 1'b0;
		#5;
		for (i = 7'd0; i < 7'd64; i = i + 7'd1) begin
			iaddr = i;
			idata = inRAM[i];
			mq    = matrix[i];
			iwren = 1'b1;
			if (inRAM[i] != 1'b0) begin
				#5;
				clk = 1'b1;
				#5;
				clk = 1'b0;
			end
		end
		$display("Time: %t", $time);
		#5;
		iwren = 1'b0;
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
		while (!rdy) begin
			clk = 1'b1;
			#5;
			clk = 1'b0;
			#5;
		end
		#5;
		assertmem();
	end
endmodule
