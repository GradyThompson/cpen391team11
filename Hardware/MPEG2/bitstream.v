module bitstream(
	// Interface to other components
	input clk,
	input reset_n,
	output reg rdy,
	input en,
	input [31:0] in_bits,
	input [4:0]  in_len,
	// Avalon Master
	output [31:0] address,
	output reg write,
	input waitrequest,
	output [31:0] writedata);
endmodule
