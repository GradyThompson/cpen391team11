module dcthash(
	input [5:0] run,
	input [15:0] level,
	output reg [15:0] vlc,
	output reg [3:0]  len,
	output reg escape
);
//  Variable length codes: Table B.15 in the MPEG-2 standard

	always @(*) begin
		if (level >= 16'd64) begin
			vlc = 16'b000001;
			len = 4'd6;
			escape = 1'b1;
		end else begin
			escape = 1'b0;
			case ({run, level[5:0]})
				{6'd0,6'd1}: begin
					vlc = 16'b10;
					len = 4'd2;
				end
				{6'd1,6'd1}: begin
					vlc = 16'b010;
					len = 4'd3;
				end
				{6'd0,6'd2}: begin
					vlc = 16'b110;
					len = 4'd3;
				end
				{6'd2,6'd1}: begin
					vlc = 16'b00101;
					len = 4'd5;
				end
				{6'd0,6'd3}: begin
					vlc = 16'b0111;
					len = 4'd4;
				end
				{6'd3,6'd1}: begin
					vlc = 16'b0011_1;
					len = 4'd5;
				end
				{6'd4,6'd1}: begin
					vlc = 16'b0001_10;
					len = 4'd6;
				end
				{6'd1,6'd2}: begin
					vlc = 16'b0011_0;
					len = 4'd5;
				end
				{6'd5,6'd1}: begin
					vlc = 16'b0001_11;
					len = 4'd6;
				end
				{6'd6,6'd1}: begin
					vlc = 16'b0000_110;
					len = 4'd7;
				end
				{6'd7,6'd1}: begin
					vlc = 16'b0000_100;
					len = 4'd7;
				end
				{6'd0,6'd4}: begin
					vlc = 16'b1110_0;
					len = 4'd5;
				end
				{6'd2,6'd2}: begin
					vlc = 16'b0000_111;
					len = 4'd7;
				end
				{6'd8,6'd1}: begin
					vlc = 16'b0000_101;
					len = 4'd7;
				end
				{6'd9,6'd1}: begin
					vlc = 16'b1111_000;
					len = 4'd7;
				end
				{6'd0,6'd5}: begin
					vlc = 16'b1110_1;
					len = 4'd5;
				end
				{6'd0,6'd6}: begin
					vlc = 16'b0001_01;
					len = 4'd6;
				end
				{6'd1,6'd3}: begin
					vlc = 16'b1111_001;
					len = 4'd7;
				end
				{6'd3,6'd2}: begin
					vlc = 16'b0010_0110;
					len = 4'd8;
				end
				{6'd10,6'd1}: begin
					vlc = 16'b1111_010;
					len = 4'd7;
				end
				{6'd11,6'd1}: begin
					vlc = 16'b0010_0001;
					len = 4'd8;
				end
				{6'd12,6'd1}: begin
					vlc = 16'b0010_0101;
					len = 4'd8;
				end
				{6'd13,6'd1}: begin
					vlc = 16'b0010_0100;
					len = 4'd8;
				end
				{6'd0,6'd7}: begin
					vlc = 16'b0001_00;
					len = 4'd6;
				end
				{6'd1,6'd4}: begin
					vlc = 16'b0010_0111;
					len = 4'd8;
				end
				{6'd2,6'd3}: begin
					vlc = 16'b1111_1100;
					len = 4'd8;
				end
				{6'd4,6'd2}: begin
					vlc = 16'b1111_1101;
					len = 4'd8;
				end
				{6'd5,6'd2}: begin
					vlc = 16'b0000_0010_0;
					len = 4'd9;
				end
				{6'd14,6'd1}: begin
					vlc = 16'b0000_0010_1;
					len = 4'd9;
				end
				{6'd15,6'd1}: begin
					vlc = 16'b0000_0011_1;
					len = 4'd9;
				end
				{6'd16,6'd1}: begin
					vlc = 16'b0000_0011_01;
					len = 4'd10;
				end
				{6'd0,6'd8}: begin
					vlc = 16'b1111_011;
					len = 4'd7;
				end
				{6'd0,6'd9}: begin
					vlc = 16'b1111_100;
					len = 4'd7;
				end
				{6'd0,6'd10}: begin
					vlc = 16'b0010_0011;
					len = 4'd8;
				end
				{6'd0,6'd11}: begin
					vlc = 16'b0010_0010;
					len = 4'd8;
				end
				{6'd1,6'd5}: begin
					vlc = 16'b0010_0000;
					len = 4'd8;
				end
				{6'd2,6'd4}: begin
					vlc = 16'b0000_0011_00;
					len = 4'd10;
				end
				{6'd3,6'd3}: begin
					vlc = 16'b0000_0001_1100;
					len = 4'd12;
				end
				{6'd4,6'd3}: begin
					vlc = 16'b0000_0001_0010;
					len = 4'd12;
				end
				{6'd6,6'd2}: begin
					vlc = 16'b0000_0001_1110;
					len = 4'd12;
				end
				{6'd7,6'd2}: begin
					vlc = 16'b0000_0001_0101;
					len = 4'd12;
				end
				{6'd8,6'd2}: begin
					vlc = 16'b0000_0001_0001;
					len = 4'd12;
				end
				{6'd17,6'd1}: begin
					vlc = 16'b0000_0001_1111;
					len = 4'd12;
				end
				{6'd18,6'd1}: begin
					vlc = 16'b0000_0001_1010;
					len = 4'd12;
				end
				{6'd19,6'd1}: begin
					vlc = 16'b0000_0001_1001;
					len = 4'd12;
				end
				{6'd20,6'd1}: begin
					vlc = 16'b0000_0001_0111;
					len = 4'd12;
				end
				{6'd21,6'd1}: begin
					vlc = 16'b0000_0001_0110;
					len = 4'd12;
				end
				{6'd0,6'd12}: begin
					vlc = 16'b1111_1010;
					len = 4'd8;
				end
				{6'd0,6'd13}: begin
					vlc = 16'b1111_1011;
					len = 4'd8;
				end
				{6'd0,6'd14}: begin
					vlc = 16'b1111_1110;
					len = 4'd8;
				end
				{6'd0,6'd15}: begin
					vlc = 16'b1111_1111;
					len = 4'd8;
				end
				{6'd1,6'd6}: begin
					vlc = 16'b0000_0000_1011_0;
					len = 4'd13;
				end
				{6'd1,6'd7}: begin
					vlc = 16'b0000_0000_1010_1;
					len = 4'd13;
				end
				{6'd2,6'd5}: begin
					vlc = 16'b0000_0000_1010_0;
					len = 4'd13;
				end
				{6'd3,6'd4}: begin
					vlc = 16'b0000_0000_1001_1;
					len = 4'd13;
				end
				{6'd5,6'd3}: begin
					vlc = 16'b0000_0000_1001_0;
					len = 4'd13;
				end
				{6'd9,6'd2}: begin
					vlc = 16'b0000_0000_1000_1;
					len = 4'd13;
				end
				{6'd10,6'd2}: begin
					vlc = 16'b0000_0000_1000_0;
					len = 4'd13;
				end
				{6'd22,6'd1}: begin
					vlc = 16'b0000_0000_1111_1;
					len = 4'd13;
				end
				{6'd23,6'd1}: begin
					vlc = 16'b0000_0000_1111_0;
					len = 4'd13;
				end
				{6'd24,6'd1}: begin
					vlc = 16'b0000_0000_1110_1;
					len = 4'd13;
				end
				{6'd25,6'd1}: begin
					vlc = 16'b0000_0000_1110_0;
					len = 4'd13;
				end
				{6'd26,6'd1}: begin
					vlc = 16'b0000_0000_1101_1;
					len = 4'd13;
				end
				{6'd0,6'd16}: begin
					vlc = 16'b0000_0000_0111_11;
					len = 4'd14;
				end
				{6'd0,6'd17}: begin
					vlc = 16'b0000_0000_0111_10;
					len = 4'd14;
				end
				{6'd0,6'd18}: begin
					vlc = 16'b0000_0000_0111_01;
					len = 4'd14;
				end
				{6'd0,6'd19}: begin
					vlc = 16'b0000_0000_0111_00;
					len = 4'd14;
				end
				{6'd0,6'd20}: begin
					vlc = 16'b0000_0000_0110_11;
					len = 4'd14;
				end
				{6'd0,6'd21}: begin
					vlc = 16'b0000_0000_0110_10;
					len = 4'd14;
				end
				{6'd0,6'd22}: begin
					vlc = 16'b0000_0000_0110_01;
					len = 4'd14;
				end
				{6'd0,6'd23}: begin
					vlc = 16'b0000_0000_0110_00;
					len = 4'd14;
				end
				{6'd0,6'd24}: begin
					vlc = 16'b0000_0000_0101_11;
					len = 4'd14;
				end
				{6'd0,6'd25}: begin
					vlc = 16'b0000_0000_0101_10;
					len = 4'd14;
				end
				{6'd0,6'd26}: begin
					vlc = 16'b0000_0000_0101_01;
					len = 4'd14;
				end
				{6'd0,6'd27}: begin
					vlc = 16'b0000_0000_0101_00;
					len = 4'd14;
				end
				{6'd0,6'd28}: begin
					vlc = 16'b0000_0000_0100_11;
					len = 4'd14;
				end
				{6'd0,6'd29}: begin
					vlc = 16'b0000_0000_0100_10;
					len = 4'd14;
				end
				{6'd0,6'd30}: begin
					vlc = 16'b0000_0000_0100_01;
					len = 4'd14;
				end
				{6'd0,6'd31}: begin
					vlc = 16'b0000_0000_0100_00;
					len = 4'd14;
				end
				{6'd0,6'd32}: begin
					vlc = 16'b0000_0000_0011_000;
					len = 4'd15;
				end
				{6'd0,6'd33}: begin
					vlc = 16'b0000_0000_0010_111;
					len = 4'd15;
				end
				{6'd0,6'd34}: begin
					vlc = 16'b0000_0000_0010_110;
					len = 4'd15;
				end
				{6'd0,6'd35}: begin
					vlc = 16'b0000_0000_0010_101;
					len = 4'd15;
				end
				{6'd0,6'd36}: begin
					vlc = 16'b0000_0000_0010_100;
					len = 4'd15;
				end
				{6'd0,6'd37}: begin
					vlc = 16'b0000_0000_0010_011;
					len = 4'd15;
				end
				{6'd0,6'd38}: begin
					vlc = 16'b0000_0000_0010_010;
					len = 4'd15;
				end
				{6'd0,6'd39}: begin
					vlc = 16'b0000_0000_0010_001;
					len = 4'd15;
				end
				{6'd0,6'd40}: begin
					vlc = 16'b0000_0000_0010_000;
					len = 4'd15;
				end
				{6'd1,6'd8}: begin
					vlc = 16'b0000_0000_0011_111;
					len = 4'd15;
				end
				{6'd1,6'd9}: begin
					vlc = 16'b0000_0000_0011_110;
					len = 4'd15;
				end
				{6'd1,6'd10}: begin
					vlc = 16'b0000_0000_0011_101;
					len = 4'd15;
				end
				{6'd1,6'd11}: begin
					vlc = 16'b0000_0000_0011_100;
					len = 4'd15;
				end
				{6'd1,6'd12}: begin
					vlc = 16'b0000_0000_0011_011;
					len = 4'd15;
				end
				{6'd1,6'd13}: begin
					vlc = 16'b0000_0000_0011_010;
					len = 4'd15;
				end
				{6'd1,6'd14}: begin
					vlc = 16'b0000_0000_0011_001;
					len = 4'd15;
				end
				{6'd1,6'd15}: begin
					vlc = 16'b0000_0000_0001_0011;
					len = 4'd0;
				end
				{6'd1,6'd16}: begin
					vlc = 16'b0000_0000_0001_0010;
					len = 4'd0;
				end
				{6'd1,6'd17}: begin
					vlc = 16'b0000_0000_0001_0001;
					len = 4'd0;
				end
				{6'd1,6'd18}: begin
					vlc = 16'b0000_0000_0001_0000;
					len = 4'd0;
				end
				{6'd6,6'd3}: begin
					vlc = 16'b0000_0000_0001_0100;
					len = 4'd0;
				end
				{6'd11,6'd2}: begin
					vlc = 16'b0000_0000_0001_1010;
					len = 4'd0;
				end
				{6'd12,6'd2}: begin
					vlc = 16'b0000_0000_0001_1001;
					len = 4'd0;
				end
				{6'd13,6'd2}: begin
					vlc = 16'b0000_0000_0001_1000;
					len = 4'd0;
				end
				{6'd14,6'd2}: begin
					vlc = 16'b0000_0000_0001_0111;
					len = 4'd0;
				end
				{6'd15,6'd2}: begin
					vlc = 16'b0000_0000_0001_0110;
					len = 4'd0;
				end
				{6'd16,6'd2}: begin
					vlc = 16'b0000_0000_0001_0101;
					len = 4'd0;
				end
				{6'd27,6'd1}: begin
					vlc = 16'b0000_0000_0001_1111;
					len = 4'd0;
				end
				{6'd28,6'd1}: begin
					vlc = 16'b0000_0000_0001_1110;
					len = 4'd0;
				end
				{6'd29,6'd1}: begin
					vlc = 16'b0000_0000_0001_1101;
					len = 4'd0;
				end
				{6'd30,6'd1}: begin
					vlc = 16'b0000_0000_0001_1100;
					len = 4'd0;
				end
				{6'd31,6'd1}: begin
					vlc = 16'b0000_0000_0001_1011;
					len = 4'd0;
				end
				default: begin
					vlc = 16'b000001;
					len = 4'd6;
					escape = 1'b1;
				end
			endcase
		end
	end

endmodule
