module xy2address (	input [9:0] row, col, 
							input [1:0] YUV, 
							output reg frame_buffer_num
							output reg [15:0] addr
							output reg [7:0] data_pos)
	assign frame_buffer_num = ((row > 10'd31) && (YUV == 2'b01)) || (YUV == 2'b10);
	always @(row, col, YUV) begin
		if (YUV == 2'b00) begin
			frame_buffer_num = 1'b0;
			addr = ((16'd640*row + col)*32'd3435973837) >> 2;
			data_pos = col - ((col*32'd3435973837) >> 2)*32'd5;
		end if (YUV == 2'b01) begin
			if (row < 10'd32) begin
				frame_buffer_num = 1'b0;
				addr = ((16'd640*(row + 16'd480) + col)*32'd3435973837) >> 2;
				data_pos = col - ((col*32'd3435973837) >> 2)*32'd5;
			end else begin
				frame_buffer_num = 1'b1;
				addr = ((16'd640*(row - 16'd32) + col)*32'd954437177 >> 2;
				data_pos = col - (col*32'd954437177) >> 2)*32'd18;
			end
		end if (YUV == 2'b10) begin
			frame_buffer_num = 1'b1;
			addr = ((16'd640*(row + 16'd448) + col)*32'd954437177 >> 2;
			data_pos = col - (col*32'd954437177) >> 2)*32'd18;
		end else begin
			frame_buffer_num = 1'bx;
			addr = {16{1'bx}};
			data_pos = {8{1'bx}};
		end
	end
end module