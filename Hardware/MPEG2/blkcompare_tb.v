module blkcompare_tb();
	reg clk, reset_n, en; // Reset is active low
	wire rdy, mreq, wren, valid; // Ready signal
	wire [7:0] baddr, waddr;  // Current macroblock address bus
	reg  [15:0] bq;    // Current macroblock data bus
	wire [15:0] wdata;
	// Previous Frame
	wire [3:0] mx, my;     // Previous frame offsets
	reg    m_wait;     // If this is true, no more requests can be sent
	reg    m_valid;    // If this is true, a new value has arrived
	reg  [7:0] mq;   // Pixel value
	// Output
	wire [17:0] accum;
	reg  [17:0] oldaccum;

	reg [15:0] BRAM [255:0];
	reg [7:0]  MRAM [255:0];
	reg [15:0] WRAM [255:0];
	reg [15:0] solnRAM [255:0];
	reg [8:0] i;

	blkcompare DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en), .baddr(baddr),
		.bq(bq), .mx(mx), .my(my), .mq(mq), .mreq(mreq), .m_wait(m_wait), .m_valid(m_valid), .waddr(waddr),
		.wdata(wdata), .wren(wren), .oldaccum(oldaccum), .accum(accum), .valid(valid));

	task assertmem;
	begin
		for (i = 9'd0; i < 9'd256; i = i + 9'd1) begin
			if (WRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0d == %0d", $time, WRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0d != %0d", $time, WRAM[i], solnRAM[i]);
			end
		end
	end
	endtask

	always @(posedge clk) begin
		bq = BRAM[baddr];
		if (wren) begin
			WRAM[waddr] = wdata;
		end
		if (mreq) begin
			m_valid = 1'b1;
			mq = MRAM[8'd16 * my + mx];
		end else begin
			m_valid = 1'b0;
		end
	end
	
	initial begin
		#10;
		$readmemh("blkcmp.in.memh",   BRAM);
		$readmemh("blkcmp.mram.memh", MRAM);
		$readmemh("blkcmp.soln.memh", solnRAM);
		oldaccum = 18'd22171;
		m_wait = 1'b0;
		m_valid = 1'b0;
		#10;
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
		if (accum === 18'd22170) begin
			$display("At time %0t, %0d == %0d", $time, accum, 18'd22170);
		end else begin
			$error("At time %0t, %0d != %0d", $time, accum, 18'd22170);
		end
		if (valid) begin
			$display("Valid signal is asserted");
		end else begin
			$error("Valid signal is not asserted");
		end
	end
	
endmodule
