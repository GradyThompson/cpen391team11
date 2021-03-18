module RGB2YUV (
		input [7:0] R,
		input [7:0] B,
		input [7:0] G,
		
		output [7:0] Y,
		output [7:0] U,
		output [7:0] V
	);
	
	assign Y = ((18'd306*R + 18'd601*G + 18'd117*B) >> 8'd10);
	assign U = ((- 18'd173*R - 18'd339*G + 18'd511*B) >> 8'd10) + 8'd128;
	assign V = ((18'd511*R - 18'd428*G - 18'd83*B) >> 8'd10) + 8'd128;
	
endmodule // RGB2YUV

