module dct1d(clk, reset_n, rdy, en, addr, wren, data, q, rstart, wstart, stride);
	input clk, reset_n;     // Reset is active low
	output [5:0] addr;      // RAM address bus
	output reg wren;        // RAM write enable
	output reg [15:0] data; // RAM write data bus
	output reg rdy;         // Ready signal
	input  en;              // Enable signal
	input  [15:0] q;        // RAM read data bus
	input  [5:0] rstart;    // Initial read address
	input  [5:0] wstart;    // Initial write address
	input  [5:0] stride;    // Input and output stride
	
	reg  [5:0] next_addr; // RAM address for next cycle
	reg  [2:0] next_state;
	reg  [3:0] next_cycle;
	wire [2:0] state;
	wire [3:0] cycle;
	wire [5:0] saved_ws; // Saved write start address
	wire [5:0] saved_st; // Saved stride
	reg  [15:0] r0in, r1in, r2in, r3in, r4in, r5in, r6in, r7in;
	reg  r0en, r1en, r2en, r3en, r4en, r5en, r6en, r7en;
	wire [15:0] r0, r1, r2, r3, r4, r5, r6, r7;
	
	register #(6)  Addr(.clk(clk), .in(reset_n  ? next_addr : 6'b0), .out(addr), .en(1'b1));
	register #(3)  State(.clk(clk), .in(reset_n ? next_state: 3'b0), .out(state), .en(1'b1));
	register #(4)  Cycle(.clk(clk), .in(reset_n ? next_cycle: 4'b0), .out(cycle), .en(1'b1));
	register #(6)  WS(.clk(clk), .in(reset_n ? wstart : 6'b0), .out(saved_ws), .en(rdy));
	register #(6)  Stride(.clk(clk), .in(reset_n ? stride : 6'b0), .out(saved_st), .en(rdy));
	// Internal registers
	register #(16) R0(.clk(clk), .in(reset_n ? r0in : 16'b0), .out(r0), .en(1'b1));
	register #(16) R1(.clk(clk), .in(reset_n ? r1in : 16'b0), .out(r1), .en(1'b1));
	register #(16) R2(.clk(clk), .in(reset_n ? r2in : 16'b0), .out(r2), .en(1'b1));
	register #(16) R3(.clk(clk), .in(reset_n ? r3in : 16'b0), .out(r3), .en(1'b1));
	register #(16) R4(.clk(clk), .in(reset_n ? r4in : 16'b0), .out(r4), .en(1'b1));
	register #(16) R5(.clk(clk), .in(reset_n ? r5in : 16'b0), .out(r5), .en(1'b1));
	register #(16) R6(.clk(clk), .in(reset_n ? r6in : 16'b0), .out(r6), .en(1'b1));
	register #(16) R7(.clk(clk), .in(reset_n ? r7in : 16'b0), .out(r7), .en(1'b1));


	always @(*) begin
		next_state = state;
		next_addr  = addr;
		next_cycle = cycle;
		rdy  = 1'b0;
		r0in = r0;
		r1in = r1;
		r2in = r2;
		r3in = r3;
		r4in = r4;
		r5in = r5;
		r6in = r6;
		r7in = r7;
		data = 16'b0;
		wren = 1'b0;
		case (state)
			3'h0: begin // Reset state
				rdy = 1'b1;
				if (en) begin
					next_state = 3'h1;
					next_cycle = 4'h0;
					next_addr  = rstart;
				end
			end
			3'h1: begin // Read from RAM: Ri = RAM[rstart + (i*stride)]
				next_addr = addr + saved_st;
				next_cycle = cycle + 4'h1;
				case (cycle)
					4'h0: r0in = q;
					4'h1: r0in = q;
					4'h2: r1in = q;
					4'h3: r2in = q;
					4'h4: r3in = q;
					4'h5: r4in = q;
					4'h6: r5in = q;
					4'h7: r6in = q;
					4'h8: begin
						r7in = q;
						next_state = 3'h2;
					end
					default: begin
					end
				endcase
			end
			3'h2: begin // Stage 1 of Approximate DCT
				r0in = r7 + r0;
				r1in = r6 + r1;
				r2in = r5 + r2;
				r3in = r4 + r3;
				r4in = r4 - r3;
				r5in = r5 - r2;
				r6in = r6 - r1;
				r7in = r7 - r0;
				next_state = 3'h3;
			end
			3'h3: begin // Stage 2 of Approximate DCT
				r0in = r0 + r3;
				r1in = r1 + r2;
				r2in = r2 - r1;
				r3in = r3 - r0;
				next_state = 3'h4;
			end
			3'h4: begin // Stage 3 of Approximate DCT
				r0in = r0 + r1;
				r1in = -(r6 + r7);
				r2in = -r3;
				r3in = r5;
				r4in = r0 - r1;
				r5in = r6 - r7;
				r6in = r2;
				r7in = r4;
				next_state = 3'h5;
				next_addr  = saved_ws;
				next_cycle = 4'h0;
			end
			3'h5: begin // Write back to RAM
				next_addr = addr + saved_st;
				next_cycle = cycle + 4'h1;
				case (cycle)
					4'h0: begin
						data = r0;
						wren = 1'b1;
					end
					4'h1: begin
						data = r1;
						wren = 1'b1;
					end
					4'h2: begin
						data = r2;
						wren = 1'b1;
					end
					4'h3: begin
						data = r3;
						wren = 1'b1;
					end
					4'h4: begin
						data = r4;
						wren = 1'b1;
					end
					4'h5: begin
						data = r5;
						wren = 1'b1;
					end
					4'h6: begin
						data = r6;
						wren = 1'b1;
					end
					4'h7: begin
						data = r7;
						wren = 1'b1;
						next_state = 3'h0;
					end
					default: begin
					end
				endcase
			end
			default: begin
				next_state = 3'h0;
			end
		endcase
	end
endmodule
