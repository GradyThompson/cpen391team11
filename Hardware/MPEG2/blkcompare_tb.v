module blkcompare_tb();
	reg clk, reset_n, en; // Reset is active low
	wire rdy, mreq, wren, valid; // Ready signal
	wire [7:0] baddr, waddr;  // Current macroblock address bus
	reg  [15:0] bq, wdata;    // Current macroblock data bus
	// Previous Frame
	wire [3:0] mx, my;     // Previous frame offsets
	reg    m_wait;     // If this is true, no more requests can be sent
	reg    m_valid;    // If this is true, a new value has arrived
	reg  [7:0] m_px;   // Pixel value
	// Output
	wire [17:0] accum;
	reg  [17:0] oldaccum;

	reg [15:0] BRAM [255:0];
	reg [15:0] WRAM [255:0];
	reg [15:0] testRAM [255:0];
	reg [15:0] solnRAM [255:0];
	reg [8:0] i;

	blkcompare DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en), .baddr(baddr),
		.bq(bq), .mx(mx), .my(my), .mq(mq), .mwait(mwait), .waddr(waddr),
		.wdata(wdata), .wren(wren), .oldaccum(oldaccum), .accum(accum), .valid(valid));

	task assertmem;
	begin
		for (i = 9'd0; i < 9'd256; i = i + 9'd1) begin
			if (testRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0d == %0d", $time, testRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0d != %0d", $time, testRAM[i], solnRAM[i]);
			end
		end
	end
	endtask

	always @(posedge clk) begin
		bq = BRAM[baddr];
		if (wren) begin
			WRAM[waddr] = wdata;
		end
	end
	
	initial begin
	end
	
endmodule
