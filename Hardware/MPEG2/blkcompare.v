module blkcompare(clk, reset_n, rdy, en, baddr, bq, mx, my, mq, mwait, waddr, wdata, wren, oldaccum, accum, valid);
	input clk, reset_n;  // Reset is active low
	output reg rdy;      // Ready signal
	input  en;           // Enable signal
	// Current macroblock
	output [7:0] baddr;  // Current macroblock address bus
	input  [15:0] bq;    // Current macroblock data bus
	// Previous Frame
	output [3:0] mx;     // Previous frame x offset
	output [3:0] my;     // Previous frame y offset
	output reg mreq;       // Assert to send request
	input    m_wait;     // If this is true, no more requests can be sent
	input    m_valid;    // If this is true, a new value has arrived
	input  [7:0] m_px;   // Pixel value
	// Output
	output [7:0] waddr;  // Output address bus
	output [15:0] wdata; // Output data bus
	output reg wren;     // Output write enable
	output [17:0] accum; // Sum of error from all pixels
	input [17:0] oldaccum; // Maximum error from previous run
	output reg valid;    // Final error is less than the old accumulator value

	reg [17:0] next_accum; // Accumulator on next cycle
	wire [15:0] error;     // Current error
	reg  [1:0] next_state;
	wire [1:0] state;
	reg  [9:0] next_x, next_y, next_mx, next_my;
	wire [9:0] X, Y, MX, MY;
	reg next_wait;
	wire waiting;

	// Previous Frame
	register #(4) MX(.clk(clk), .in(reset_n  ? next_mx : 10'b0), .out(mx), .en(1'b1));
	register #(4) MY(.clk(clk), .in(reset_n  ? next_my : 10'b0), .out(my), .en(1'b1));
	// Current Macroblock
	register #(4) X(.clk(clk), .in(reset_n  ? next_x : 10'b0), .out(x), .en(1'b1));
	register #(4) Y(.clk(clk), .in(reset_n  ? next_y : 10'b0), .out(y), .en(1'b1));
	// Misc
	register #(1) Waiting(.clk(clk), .in(reset_n ? next_wait : 1'b0), .out(waiting), .en(1'b1));
	register #(18) Accum(.clk(clk), .in(reset_n  ? next_accum : 16'b0), .out(accum), .en(1'b1));
	register #(2) State(.clk(clk), .in(reset_n ? next_state : 2'b0), .out(state), .en(1'b1));

	assign baddr = {next_y, next_x};
	assign waddr = {y, x};
	assign error = (bq > {8'b0, m_px}) ? (bq - {8'b0, m_px}) : ({8'b0, m_px} - bq);
	
	always @(*) begin
		next_mx = mx;
		next_my = my;
		next_x = x;
		next_y = y;
		next_wait = waiting;
		next_accum = accum;
		next_state = state;
		rdy = 1'b0;
		wren = 1'b0;
		valid = 1'b0;
		waddr = 8'b0;
		mreq = 1'b0;
		case (state)
			2'h0: begin // Reset state
				rdy = 1'b1;
				if (en) begin
					next_state = 2'h1;
					next_mx = 4'h0;
					next_my = 4'h0;
					next_x = 4'h0;
					next_y = 4'h0;
					next_wait = 1'b0;
					next_accum = 18'h0;
				end
			end
			2'h1: begin // Iterate over every (x, y) from (0, 0) to (15, 15)
				if ($signed(accum) > $signed(oldaccum)) begin
					next_state = 2'h2;
				end

				if ((!m_wait) && (!waiting)) begin
					mreq = 1'b1;
					next_mx = mx + 4'b1;
					if (mx == 4'd15) begin
						if (my == 4'd15) begin
							next_wait = 1'b1;
						end else begin
							next_mx = 4'b0;
							next_my = my + 4'b1;
						end
					end
				end
				
				if (m_valid) begin
					wren = 1'b1;
					next_accum = accum + error;
					wdata = bq - {8'b0, m_px};
					next_x = x + 4'd1;
					if (x == 4'd15) begin
						next_x = 4'd0;
						next_y = y + 4'd1;
					end
				end
			end
			2'h2: begin // Wait for all requests to finish
				if ((mx == x) && (my == y)) begin
					if ($signed(accum) > $signed(oldaccum)) begin
						next_state = 2'h0;
					end else begin
						next_state = 2'h2;
					end
				end else begin
					if (m_valid) begin
						next_x = x + 4'd1;
						if (x == 4'd15) begin
							next_x = 4'd0;
							next_y = y + 4'd1;
						end
					end
				end
			end
			2'h3: begin // Valid ready state
				rdy = 1'b1;
				valid = 1'b1;
				if (en) begin
					next_state = 2'h1;
					next_mx = 4'h0;
					next_my = 4'h0;
					next_x = 4'h0;
					next_y = 4'h0;
					next_wait = 1'b0;
					next_accum = 18'h0;
				end
			end
		endcase
	end
endmodule
