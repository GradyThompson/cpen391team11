`timescale 1 ns / 1 ns
module dct2d_tb();
	reg clk, reset_n, en;
	wire rdy, wwren;
	wire [5:0] iaddr, maddr, waddr;
	reg  [7:0] iq;
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

	dct2d DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en), .iaddr(iaddr), .iq(iq), .maddr(maddr), .mq(mq), .waddr(waddr), .wdata(wdata), .wwren(wwren));

	always @(*) begin
		iq = inRAM[iaddr];
		mq = matrix[maddr];
	end

	always @(posedge clk) begin
		if (wwren) begin
			testRAM[waddr] = wdata;
		end
	end

	initial begin
		#10;
		$readmemh("dct2d.in.memh", inRAM);
		$readmemh("matrix.memh", matrix);
		$readmemh("dct2d.soln.memh", solnRAM);
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
