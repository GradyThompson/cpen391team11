module register_tb();
	reg [7:0] in8;
	wire [7:0] out8;
	reg [15:0] in16;
	wire [15:0] out16;
	reg clk, en;
	
	register #(8) R8(.in(in8), .out(out8), .clk(clk), .en(en));
	register #(16) R16(.in(in16), .out(out16), .clk(clk), .en(en));
	
	task assert8(input [7:0] in, input [7:0] expected);
	begin
		if (in === expected) begin
			$display("At time %0t, %0d == %0d", $time, in, expected);
		end else begin
			$error("At time %0t, %0d != %0d", $time, in, expected);
		end
	end
	endtask

	task assert16(input [15:0] in, input [15:0] expected);
	begin
		if (in === expected) begin
			$display("At time %0t, %0d == %0d", $time, in, expected);
		end else begin
			$error("At time %0t, %0d != %0d", $time, in, expected);
		end
	end
	endtask
	
	initial begin
		in8  = 8'b0;
		in16 = 16'b0;
		clk  = 1'b0;
		en   = 1'b1;
		#5;
		clk  = 1'b1;
		#5;
		assert8(out8, 8'b0);
		assert16(out16, 16'b0);
		#5;
		clk  = 1'b0;
		en   = 1'b0;
		in16 = 16'hABCD;
		in8  = 8'hEF;
		en   = 1'b0;
		#5;
		clk = 1'b1;
		#5;
		assert8(out8, 8'b0);
		assert16(out16, 16'b0);
		#5;
		clk  = 1'b0;
		en   = 1'b1;
		in16 = 16'h1234;
		in8  = 8'h56;
		en   = 1'b1;
		#5;
		clk = 1'b1;
		#5;
		assert8(out8, 8'h56);
		assert16(out16, 16'h1234);
		#5;
	end
	
endmodule
