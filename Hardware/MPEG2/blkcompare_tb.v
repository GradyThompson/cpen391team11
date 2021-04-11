module blkcompare_tb();
	reg clk, reset_n, en; // Reset is active low
	wire rdy, mreq, wren, valid; // Ready signal
	wire [3:0] bx, by, wx, wy;
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
	wire [1:0]  cc;

	reg [15:0] BRAM [383:0]; // Enough space to store Y, U, and V
	reg [7:0]  MRAM [383:0];
	reg [15:0] WRAM [383:0];
	reg [15:0] solnRAM [383:0];
	reg [8:0] i;
	reg [9:0] offset;
	reg [8:0] mult;

	blkcompare DUT(.clk(clk), .reset_n(reset_n), .rdy(rdy), .en(en), .cc(cc), .bx(bx), .by(by),
		.bq(bq), .mx(mx), .my(my), .mq(mq), .mreq(mreq), .m_wait(m_wait), .m_valid(m_valid), .wx(wx), .wy(wy),
		.wdata(wdata), .wren(wren), .oldaccum(oldaccum), .accum(accum), .valid(valid));

	task assertmem;
	begin
		for (i = 9'd0; i < 9'd383; i = i + 9'd1) begin
			if (WRAM[i] === solnRAM[i]) begin
				$display("At time %0t, %0d == %0d", $time, WRAM[i], solnRAM[i]);
			end else begin
				$error("At time %0t, %0d != %0d", $time, WRAM[i], solnRAM[i]);
			end
		end
	end
	endtask

	always @(*) begin
		case (cc)
			2'b00: begin
				offset = 9'b0;
				mult = 8'd16;
			end
			2'b01: begin
				offset = 9'd256;
				mult = 8'd8;
			end
			2'b10: begin
				offset = 9'd320;
				mult = 8'd8;
			end
			default: begin
				offset = 9'd0;
			end
		endcase
	end
	
	always @(posedge clk) begin
		bq = BRAM[(mult * by + bx) + offset];
		if (wren) begin
			WRAM[(mult * wy + wx) + offset] = wdata;
		end
		if (mreq) begin
			m_valid = 1'b1;
			mq = MRAM[(mult * my + mx) + offset];
		end else begin
			m_valid = 1'b0;
		end
	end
	
	initial begin
		#10;
		$readmemh("blkcmp.in.memh",   BRAM);
		$readmemh("blkcmp.mram.memh", MRAM);
		$readmemh("blkcmp.soln.memh", solnRAM);
		oldaccum = 18'd34158;
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
		if (accum === 18'd34157) begin
			$display("At time %0t, %0d == %0d", $time, accum, 18'd34157);
		end else begin
			$error("At time %0t, %0d != %0d", $time, accum, 18'd34157);
		end
		if (valid) begin
			$display("Valid signal is asserted");
		end else begin
			$error("Valid signal is not asserted");
		end
	end
	
endmodule
