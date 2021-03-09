module RGB2YUV (
		input [7:0] R,
		input [7:0] B,
		input [7:0] G,
		
		output [7:0] Y,
		output [7:0] U,
		output [7:0] V
	);
	
	assign Y = (8'd128 + 8'd66*R + 8'b129*G + 8'b25*B) >> 8'd8 + 8'd16;
	assign U = (8'd128 - 8'd38*R - 8'b74*G + 8'b112*B) >> 8'd8 + 8'd16;
	assign V = (8'd128 + 8'd112*R - 8'b94*G - 8'b18*B) >> 8'd8 + 8'd16;
	
end module