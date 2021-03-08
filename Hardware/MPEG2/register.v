module register(clk, in, en, out);
	parameter WIDTH = 8;
	input [WIDTH-1:0] in;
	input en;
	input clk;
	output reg [WIDTH-1:0] out;

	// WIDTH bits wide D Flip Flop with enable
	always @(posedge clk) begin
		if (en) begin
			out <= in;
		end
	end
endmodule
