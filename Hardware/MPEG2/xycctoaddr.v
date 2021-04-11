module XYCCtoAddr(
	input [3:0] x,
	input [3:0] y,
	input [1:0] cc,
	output reg [8:0] addr
);
	always @(*) begin
		case (cc)
			2'b00: begin
				addr = {1'b0, y, x};
			end
			2'b01: begin
				addr = 9'd256 + {y[2:0], x[2:0]};
			end
			2'b10: begin
				addr = 9'd320 + {y[2:0], x[2:0]};
			end
			default: begin
				addr = 9'd0;
			end
		endcase
	end
endmodule
