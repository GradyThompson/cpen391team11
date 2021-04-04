module XY2ADDRESS (	input [9:0] row, col, 
			output U_frame_buffer_num,
			output reg [15:0] Y_addr,
			output reg [5:0]  Y_data_pos,
			output reg [15:0] U_addr,
			output reg [7:0]  U_data_pos,
			output reg [12:0] V_addr,
			output reg [7:0]  V_data_pos);
   
   /*
    U_frame_buffer_num is the frame_buffer that the pixel's U value is in, Y is strictly in buffer 1
    and V is strictly in buffer 2
    */		
   wire [9:0] 				  hrow, hcol;
   
   assign hrow = row >> 1;
   assign hcol = col >> 1;
   
   assign U_frame_buffer_num = hrow > 10'd31;
   
   /*
    Data position is assigned the position in the data array in memory that is the start
    of that pixels data (each pixel has 8 bits)
    */
   
   always @(row, col) begin
      Y_addr = ((16'd640*row + col)*64'd838861) >> 22;
      Y_data_pos = (col - ((col*64'd838861) >> 22)*32'd5) << 3;
      if (hrow < 10'd64) begin
	 U_addr = (((32'd480*32'd640 + 32'd320*hrow + hcol)*64'd838861) >> 22);
	 U_data_pos = (hcol - ((hcol*64'd838861) >> 22)*32'd5) << 3;
      end else begin
	 U_addr = ((32'd320*(hrow - 32'd64) + hcol)*64'd233017) >> 22;
	 U_data_pos = (hcol - ((hcol*64'd233017) >> 22)*32'd18) << 3;
      end
      V_addr = ((32'd320*(hrow + 32'd176) + hcol)*64'd233017) >> 22;
      V_data_pos = (hcol - ((hcol*64'd233017) >> 22)*32'd18) << 3;
   end
endmodule
