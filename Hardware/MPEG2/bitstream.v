module bitstream(
	input clk,
	input reset_n,
	// Data interface
	output reg rdy,
	input en,
	input [15:0] in_bits,
	input [3:0]  in_len,
	// Control interface
	output full,
	input  setaddr,
	input  [31:0] abase,
	input  [31:0] aend,
	// Avalon Master
	output [31:0] address,
	output reg write,
	input waitrequest,
	output [15:0] writedata);

	reg  [31:0] next_wptr;
	reg  [15:0] next_wdata, next_in;
	wire [15:0] in;
	reg  [1:0] next_state;
	wire [1:0] state;

	reg  [3:0] next_len;
	wire [3:0] len;
	wire [4:0] curr_len, temp_len, new_len;

	wire [31:0] shift_temp;
	wire [31:0] shift_in;
	
	register #(32) WPTR(.clk(clk), .in(reset_n ? next_wptr : 32'b0), .out(address), .en(1'b1));
	register #(16) DATA(.clk(clk), .in(reset_n ? next_wdata : 16'b0), .out(writedata), .en(1'b1));
	register #(16) INPUT(.clk(clk), .in(reset_n ? next_in : 16'b0), .out(in), .en(1'b1));
	register #(4)  LEN(.clk(clk), .in(reset_n ? next_len : 4'b0), .out(len), .en(1'b1));
	register #(2)  STATE(.clk(clk), .in(reset_n ? next_state : 2'b0), .out(state), .en(1'b1));

	assign full = (address == aend);
	assign shift_temp = {writedata, 16'b0};
	assign shift_in  = {16'b0, in_bits};
	assign curr_len  = (in_len == 4'b0) ? 5'd16 : {1'b0,in_len};
	assign temp_len  = {1'b0, len} + curr_len - 5'd16;
	assign new_len   = {1'b0, len} + curr_len;
	
	always @(*) begin
		write = 1'b0;
		rdy   = 1'b0;
		next_wptr  = address;
		next_wdata = writedata;
		next_in    = in;
		next_len   = len;
		next_state = state;
		case (state)
			2'b00: begin
				rdy = (address != aend);
				if (setaddr) begin
					next_wptr = abase;
				end else if (en) begin
					if (new_len >= 5'd16) begin
						next_state = 2'b01;
						next_in = in_bits;
						next_wdata = shift_temp[15+len-:16] | shift_in[new_len-1-:16];
						next_len = temp_len[3:0];
					end else begin
						next_wdata = shift_temp[31-in_len-:16] | in_bits;
						next_len = new_len[3:0];
					end
				end
			end
			2'b01: begin
				if (waitrequest == 1'b0) begin
					next_state = 2'b00;
					write = 1'b1;
					next_wdata = in;
					next_wptr = address + 32'd2;
				end
			end
			default: begin
				next_state = 2'b00;
			end
		endcase
	end
endmodule
