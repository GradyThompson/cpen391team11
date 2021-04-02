module dct2d(clk, reset_n, rdy, en, iaddr, iq, maddr, mq, waddr, wdata, wwren);
	input clk, reset_n;  // Reset is active low
	output reg rdy;      // Ready signal
	input  en;           // Enable signal
	output reg [5:0] iaddr;  // Input address bus
	input  [15:0] iq;        // Input data bus
	output reg [5:0] maddr;  // Matrix address bus
	input  [15:0] mq;        // Matrix data bus
	output reg [5:0] waddr;  // Output address bus
	output [15:0] wdata; // Output data bus
	output reg wwren;    // Output write enable
	
	reg  [5:0] next_addr; // Address for next cycle
	wire [5:0] addr;
	reg  [2:0] next_state;
	wire [2:0] state;
	reg  [15:0] ram_data;
	reg  ram_wren;
	wire [15:0] ram_q;
	wire drdy;            // 1D DCT ready
	reg  den, dsel, isel; // 1D DCT enable and select
	reg  [5:0] dstart;    // 1D DCT start
	reg  [5:0] dstride;   // 1D DCT stride
	wire [15:0] ddata;    // 1D DCT data bus
	wire dwren;           // 1D DCT write enable
	wire [5:0]  daddr;    // 1D DCT address bus
	wire [31:0] mul_out = $signed(mq) * $signed(ram_q);  // Multiplier out
	wire [31:0] biased_mul = mul_out[31] ? mul_out + 32'h0000FFFF : mul_out; // Bias for right shift

	register #(6)  Addr(.clk(clk), .in(reset_n  ? next_addr : 6'b0), .out(addr), .en(1'b1));
	register #(3)  State(.clk(clk), .in(reset_n ? next_state: 3'b0), .out(state), .en(1'b1));
	dctram RAM(.address(dsel ? daddr : addr), .clock(clk), .data(dsel ? ddata : ram_data), .wren(dsel ? dwren : ram_wren), .q(ram_q));
	dct1d  DCT(.clk(clk), .reset_n(reset_n), .rdy(drdy), .en(den), .addr(daddr), .wren(dwren), .data(ddata), .q(isel ? iq : ram_q), .rstart(dstart), .wstart(dstart), .stride(dstride));

	always @(*) begin
		next_state = state;
		next_addr  = addr;
		rdy  = 1'b0;
		ram_wren = 1'b0;
		ram_data = 16'b0;
		dsel = 1'b0;
		isel = 1'b0;
		wwren = 1'b0;
		den = 1'b0;
		dstart = 6'h0;
		dstride = 6'h0;
		iaddr = 6'h0;
		maddr = 6'h0;
		waddr = 6'h0;
		case (state)
			3'h0: begin // Reset state
				rdy = 1'b1;
				if (en) begin
					next_state = 3'h1;
					next_addr  = 6'h0;
				end
			end
			3'h1: begin // 1D DCT over rows
				dstart = addr;
				iaddr = daddr;
				dstride = 6'h1;
				dsel = 1'b1;
				isel = 1'b1;
				if (drdy) begin
					den = 1'b1;
					if (addr == 6'd56) begin
						next_state = 3'h2;
						next_addr = 6'd0;
					end else begin
						next_addr = addr + 6'h8;
					end
				end
			end
			3'h2: begin // Wait for drdy
				isel = 1'b1;
				dsel = 1'b1;
				iaddr = daddr;
				if (drdy) begin
					next_state = 3'h3;
				end
			end 
			3'h3: begin // 1D DCT over columns
				dstart = addr;
				dstride = 6'h8;
				dsel = 1'b1;
				if (drdy) begin
					if (addr == 6'h8) begin
						next_state = 3'h5;
						next_addr = 6'd0;
					end else begin
						den = 1'b1;
						next_addr = addr + 6'h1;
					end
				end
			end
			3'h5: begin // Quantization and output
				wwren = 1'b1;
				maddr = addr;
				waddr = addr - 6'h1;
				next_addr = addr + 6'h1;
				if (addr == 6'd63) begin
					next_addr = 6'h0;
					next_state = 3'h0;
				end
			end
			default: begin
				next_state = 3'h0;
			end
		endcase
	end
	
	assign wdata = biased_mul[31:16];
endmodule
