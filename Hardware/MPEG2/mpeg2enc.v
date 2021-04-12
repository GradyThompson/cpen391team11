module mpeg2enc (
    input clock,
    input reset_n,
    input unsigned [7:0] addr,
    input rd_en,
    input wr_en,
    output reg unsigned [31:0] dataOut,
    input unsigned [31:0] dataIn
);


	reg next_buf1, next_buf2, en;
	wire buf1, buf2;
	reg [3:0] next_state;
	wire [3:0] state;
	
	register #(1) BUF1(.clk(clock), .in(next_buf1), .out(buf1), .en(1'b1));
	register #(1) BUF2(.clk(clock), .in(next_buf2), .out(buf2), .en(1'b1));
	register #(4) STATE(.clk(clock), .in(next_state), .out(state), .en(1'b1));

	always @(posedge clock) begin
		en = 1'b0;
		if (wr_en == 1'b1) begin
			if (addr == 8'b0) begin
				if (state == 4'b0) begin
					en = 1'b1; // Ready/enable signal
				end
			end
		end
	end

    always @(*) begin
		dataOut <= 32'b0;
		if (rd_en == 1'b1) begin
			if (addr == 8'b0) begin
				dataOut = {29'b0, buf2, buf1, state == 4'h0};
			end
		end
		case (state)
			4'h0: begin
				if (en) begin
					next_state = 4'h1;
				end
			end
			default: begin
				next_state = 4'h0;
			end
		endcase
	end

endmodule
