module rleenc(
	input clk,
	input reset_n,
	output reg rdy,
	input en,
	// RAM interface to DCT output
	output [5:0] addr,
	input [15:0] q,
	// Interface to hash table
	input  h_rdy,
	output reg h_en,
	output [15:0] h_val,
	output [5:0]  h_len,
	output reg h_end, // Signal end of block
	output h_dc   // If current value is the DC coefficient
);

	reg  [2:0] next_x, next_y;
	wire [2:0] x, y;
	reg  [5:0] next_rle;
	reg  [1:0] next_state;
	wire [1:0] state;
	reg next_neg;
	wire neg;

	// X and Y coordinate in the DCT block
	register #(3) X(.clk(clk), .in(reset_n ? next_x : 3'b0), .out(x), .en(1'b1));
	register #(3) Y(.clk(clk), .in(reset_n ? next_y : 3'b0), .out(y), .en(1'b1));
	// Select moving up or down the diagonal
	register #(1) NEG(.clk(clk), .in(reset_n ? next_neg : 1'b0), .out(neg), .en(1'b1));
	// Number of zeros seen so far
	register #(6) RLE(.clk(clk), .in(reset_n ? next_rle : 6'b0), .out(h_len), .en(1'b1));
	// Current state
	register #(2) STATE(.clk(clk), .in(reset_n ? next_state : 2'b0), .out(state), .en(1'b1));

	assign addr = { next_y, next_x };
	assign h_dc = {y, x} == 6'b0;
	assign h_val = q;
	
	always @(*) begin
		rdy = 1'b0;
		next_state = state;
		next_x = x;
		next_y = y;
		next_neg = neg;
		next_rle = h_len;
		h_end = 1'b0;
		h_en  = 1'b0;
		case (state)
			2'b00: begin
				rdy = 1'b1;
				if (en) begin
					next_state = 2'b01;
					next_x = 3'd0;
					next_y = 3'd0;
					next_neg = 1'b0;
					next_rle = 6'd0;
				end
			end
			2'b01: begin
				// Logic to select the next address
				if ((y == 3'd7) && (x[0] == 1'b0)) begin
					next_x = x + 3'b1;
					next_neg = 1'b0;
				end else if ((x == 3'd7) && (y[0] == 1'b1)) begin
					next_y = y + 3'b1;
					next_neg = 1'b1;
				end else if ((y == 3'b0) && (x[0] == 1'b0)) begin // Even x, y = 0
					next_x = x + 3'b1;
					next_neg = 1'b1;
				end else if ((x == 3'b0) && (y[0] == 1'b1)) begin // Odd y, x = 0
					next_y = y + 3'b1;
					next_neg = 1'b0;
				end else begin
					if (neg) begin // Go diagonally downward
						next_x = x - 3'b1;
						next_y = y + 3'b1;
					end else begin // Go diagonally upward
						next_x = x + 3'b1;
						next_y = y - 3'b1;
					end
				end
				
				// Logic to record the RLE values
				if (q == 6'b0) begin // Count run of zeros
					next_rle = h_len + 6'h1;
					if ((x == 3'd7) && (y == 3'd7)) begin // Last digit is a zero
						h_end = 1'b1; // Signal end of block
						if (h_rdy) begin
							h_en = 1'b1;
							next_state = 2'b00;
						end else begin // Wait state
							next_x = x;
							next_y = y;
						end
					end
				end else begin // Push value to hash table
					if (h_rdy) begin 
						h_en = 1'b1;
						next_rle = 6'd0;
						if ((x == 3'd7) && (y == 3'd7)) begin // Last digit is non-zero
							h_end = 1'b1; // Signal end of block
							next_state = 2'b00;
						end
					end else begin // Wait state
						next_x = x;
						next_y = y;
					end
				end
			end
			default: begin
				next_state = 2'b00;
			end
		endcase
	end

endmodule
