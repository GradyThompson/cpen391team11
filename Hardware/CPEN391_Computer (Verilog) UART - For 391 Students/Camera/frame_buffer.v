`define S0 3'b000 //Reset
`define S1 3'b010 //Y
`define S2 3'b011 //U
`define S3 3'b100 //V

module frame_buffer (clock, waitrequest, resetn, Y, U, V, col, row, addr, data, write);
   input waitrequest;
   input clock, resetn;
   input [7:0] Y;
   input [7:0] U;
   input [7:0] V;
   input [11:0] row;
   input [11:0] col;
   output reg [26:0] addr;
   output reg [7:0]  data;
   output 	 write;

   reg [2:0] 	 state;
   wire 	 nextU, nextV;

   assign nextU = (col[0] == 1'b0) && (row[0] == 1'b0);
   assign nextV = (col[0] == 1'b1) && (row[0] == 1'b0);
   assign write = state != `S0;
   
   always @(posedge clock) begin
     if (!resetn)
       state = `S0;
     else begin
       casex ({state, nextU, nextV})
	 {`S0, 2'bxx}: begin
	    state <= `S1;
	    data <= Y;
	    addr <= row*25'd3264 + col;
	 end
	 
	 {`S1, 2'b10}: begin
	    state <= `S2;
	    data <= U;
	    addr <= 25'd7990272 + row*25'd3264 + col;
	 end
	 {`S1, 2'b01}: begin
	    state <= `S3;
	    data <= V;
	    addr <= 25'd9987839 + row*25'd3264 + col;
	 end
	 {`S1, 2'b00}: begin
	    state <= `S1;
	    data <= Y;
	    addr <= row*25'd3264 + col;
	 end
	 {`S2, 2'bxx}: begin
	    state <= `S1;
	    data <= Y;
	    addr <= row*25'd3264 + col;
	 end
	 {`S3, 2'bxx}: begin
	    state <= `S1;
	    data <= Y;
	    addr <= row*25'd3264 + col;
	 end
	 default: begin
	    state <= 3'bxxx;
	    data <= {8{1'bx}};
	    addr <= {25{1'bx}};
	 end
       endcase // casex (state)
     end // else: !if(!resetn)
   end
endmodule
