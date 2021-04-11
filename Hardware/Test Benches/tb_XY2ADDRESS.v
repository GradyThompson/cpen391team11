module tb_XY2ADDRESS();

   //Test bench for row/col to address converting module
   reg [9:0] row, col;
   wire U_frame_buffer_num;
   wire [15:0] Y_addr, U_addr;
   wire [5:0]  Y_data_pos;
   wire [7:0]  U_data_pos, V_data_pos;
   wire [12:0] V_addr;
   
   XY2ADDRESS DUT(row, col, U_frame_buffer_num, Y_addr, Y_data_pos, U_addr, U_data_pos, V_addr, V_data_pos);

   initial begin
      
      //Test 1
      row = 0;
      col = 0;
      #1;
      if (Y_addr !== 16'b0) $display("FAILED: Y_addr is %0d, when it should be 0 for Test 1", Y_addr);
      if (U_addr !== 16'd61440) $display("FAILED: U_addr is %0d, when it should be 61440 for Test 1", U_addr);
      if (V_addr !== 13'd3128) $display("FAILED: V_addr is %0d, when it should be 3128 for Test 1", V_addr);

      if (Y_data_pos !== 6'b0) $display("FAILED: Y_data_pos is %0d, when it should be 0 for Test 1", Y_data_pos);
      if (U_data_pos !== 8'b0) $display("FAILED: U_data_pos is %0d, when it should be 0 for Test 1", U_data_pos);
      if (V_data_pos !== 8'b0) $display("FAILED: V_data_pos is %0d, when it should be 0 for Test 1", V_data_pos);

      #1;

      //Test 2

      $display("Done Tests");
   end
endmodule // tb_XY2ADDRESS
