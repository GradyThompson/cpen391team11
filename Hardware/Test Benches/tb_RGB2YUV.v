module tb_RGB2YUV();

   //Test bench for the RGB to YUV converting module
   reg [7:0] R, B, G;
   wire [7:0] Y, U, V;

   RGB2YUV DUT (.R(R), .B(B), .G(G), .Y(Y), .U(U), .V(V));
   
   initial begin
      //Test 1
      R = 8'd0;
      B = 8'd0;
      G = 8'd0;
      

      #1;

      if (Y !== 8'd0) $display("FAILED: Y is %0d, when it should be 0 for Test 1", Y);
      if (U !== 8'd128) $display("FAILED: U is %0d, when it should be 128 for Test 1", U);
      if (V !== 8'd128) $display("FAILED: V is %0d, when it should be 128 for Test 1", V);

      #1;

      //Test 2
      R = 8'd255;
      B = 8'd0;
      G = 8'd0;

      #1;

      if (Y !== 8'd76) $display("FAILED: Y is %d, when it should be 76 for Test 2", Y);
      if (U !== 8'd84) $display("FAILED: U is %d, when it should be 84 for Test 2", U);
      if (V !== 8'd255) $display("FAILED: V is %d, when it should be 255 for Test 2", V);

      #1;

      //Test 3
      R = 8'd0;
      B = 8'd255;
      G = 8'd0;

      #1;

      if (Y !== 8'd29) $display("FAILED: Y is %d, when it should be 29 for Test 3", Y);
      if (U !== 8'd255) $display("FAILED: U is %d, when it should be 255 for Test 3", U);
      if (V !== 8'd107) $display("FAILED: V is %d, when it should be 107 for Test 3", V);

      #1;

      //Test 4
      R = 8'd0;
      B = 8'd0;
      G = 8'd255;

      #1;

      if (Y !== 8'd149) $display("FAILED: Y is %d, when it should be 149 for Test 4", Y);
      if (U !== 8'd43) $display("FAILED: U is %d, when it should be 43 for Test 4", U);
      if (V !== 8'd21) $display("FAILED: V is %d, when it should be 21 for Test 4", V);

      #1;

      //Test 5
      R = 8'd255;
      B = 8'd255;
      G = 8'd255;

      #1;

      if (Y !== 8'd255) $display("FAILED: Y is %d, when it should be 255 for Test 5", Y);
      if (U > 8'd129 || U < 8'd127) $display("FAILED: U is %d, when it should be 128 for Test 5", U);
      if (V !== 8'd128) $display("FAILED: V is %d, when it should be 128 for Test 5", V);

      #1;

      //Test 6
      R = 8'd112;
      B = 8'd10;
      G = 8'd70;

      #1;

      if (Y !== 8'd75) $display("FAILED: Y is %d, when it should be 75 for Test 6", Y);
      if (U > 8'd91 || U < 8'd89) $display("FAILED: U is %d, when it should be 90 for Test 6", U);
      if (V !== 8'd153) $display("FAILED: V is %d, when it should be 153 for Test 6", V);

      #1;

      $display("Finished tests");
   end

endmodule
