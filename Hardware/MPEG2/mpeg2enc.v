module mpeg2enc (
    input clock,
    input reset_n,
//  Avalon Slave interface
    input [8:0] addr,
    input rd_en,
    input wr_en,
    output reg [31:0] dataOut,
    input [31:0] dataIn,
//  Motion estimator - Camera conduit
	output [9:0] mx,
	output [9:0] my,
	output [1:0] cc,
	input  [7:0] px,
//  Avalon Master motion estimator interface
	output me_read,
	output me_write,
	output [31:0] me_addr,
	output [7:0]  me_wdata,
	input  [7:0]  me_rdata,
	input  me_waitrequest,
	input  me_readdatavalid,
//  Avalon Master bitstream interface
	output [31:0] bs_addr,
	output bs_write,
	input  bs_waitrequest,
	output [15:0] bs_wdata
);

	parameter SDRAM_BASE_1 = 32'd00000000;
	parameter SDRAM_BASE_2 = 32'd00460800;
	parameter WIDTH  = 10'd640;
	parameter HEIGHT = 10'd640;
	reg switch; // Switch motion estimator between base 1 and 2
	reg swap;   // Switch bitstream between buffer 1 and 2
	wire blksel; // Select buffer 1 or 2
	// Global variables: Register 0 when read and written
	reg next_buf1, next_buf2, en;
	wire buf1, buf2;

	// bsel: Give RLE encoder or this module control over bitstream
	// dsel: Give RLE encoder or DCT module control over RAM
	reg bsel, dsel;
	
	// Module state
	reg [3:0] next_state;
	wire [3:0] state;

	wire brdy, full;
	wire [15:0] rleq;
	reg ben, bs_len;
	reg  men, m_wb, next_intra;
	reg  [9:0] next_ix, next_iy;
	wire [9:0] ix, iy, xmin, ymin;
	wire [15:0] h_val, b_val;
	wire [3:0]  h_len, b_len;
	wire escape, mrdy, intra;
	
	wire [5:0] idctwaddr, dctwaddr;
	wire [7:0] raddr, dctaddr;
	wire [15:0] iq, dctdata, dctwdata;
	wire dctwren, wwren;

	reg  [2:0] next_block;
	reg  [15:0] bs_in;
	wire [2:0] block;
	wire [5:0] maddr, rleaddr;
	wire [15:0] mq, nq, imq, inq;
	reg  mwren, nwren, imwren, inwren;

	wire [31:0] b1_base, b2_base, b1_end, b2_end;

	reg setaddr;
	wire rrdy;
	reg ren;
	wire hrdy;
	wire hen;
	wire eob; // End of block
	wire hdc; // Coefficient is DC
	wire [31:0] mbase;
	wire dctrdy;
	reg dcten;
	wire idctrdy;
	reg idcten;
	wire [5:0] iaddr, dctmaddr, idctaddr;

	
	// If buffer 1 is full
	register #(1) BUF1(.clk(clock), .in(next_buf1), .out(buf1), .en(1'b1));
	// If buffer 2 is full
	register #(1) BUF2(.clk(clock), .in(next_buf2), .out(buf2), .en(1'b1));
	register #(4) STATE(.clk(clock), .in(next_state), .out(state), .en(1'b1));

	// Quantization matrices
	dctram INTRA_M(.clock(clock), .address(maddr), .data(dataIn[15:0]), .wren(mwren), .q(mq));
	dctram NINTR_M(.clock(clock), .address(maddr), .data(dataIn[15:0]), .wren(nwren), .q(nq));
	dctram INTRA_INV_M(.clock(clock), .address(maddr), .data(dataIn[15:0]), .wren(imwren), .q(imq));
	dctram NINTR_INV_M(.clock(clock), .address(maddr), .data(dataIn[15:0]), .wren(inwren), .q(inq));
	// Output of DCT
	dctram DCT_OUT(.clock(clock), .address(dsel ? dctwaddr : rleaddr), .data(dctwdata), .wren(wwren),. q(rleq));

	bitstream BITSTREAM(.clk(clock), .reset_n(reset_n), .rdy(brdy), .en(ben),
	.in_bits(bsel ? b_val : bs_in), .in_len(bsel ? b_len : bs_len), .full(full),
	.setaddr(setaddr), .abase(blksel ? b2_base : b1_base), .aend(blksel ? b2_end : b1_end),
	.address(bs_addr), .write(bs_write), .waitrequest(bs_waitrequest), .writedata(bs_wdata));

	dcthash HASHMAP(.run(h_len), .level($signed(h_val) >= 16'd0 ? h_val : -h_val), .vlc(b_val), .len(b_len), .escape(escape));

	rleenc RLEENC(.clk(clock), .reset_n(reset_n), .rdy(rrdy), .en(ren), .addr(rleaddr),
	.q(rleq), .h_rdy(hrdy), .h_en(hen), .h_val(h_val), .h_len(h_len), .h_end(eob), .h_dc(hdc));

	motionestimator ME(.clk(clock), .reset_n(reset_n), .rdy(mrdy), .en(men),
	.writeback(m_wb), .ix(ix), .iy(iy), .base(mbase), .intra(intra), .read(me_read),
	.write(me_write), .addr(me_addr), .writedata(me_wdata), .readdata(me_rdata),
	.waitrequest(me_waitrequest), .readdatavalid(me_readdatavalid), .x(mx),
	.y(my), .cc(cc), .px(px), .xmin(xmin), .ymin(ymin), .raddr(raddr), .q(iq),
	.dctaddr(dctaddr), .dctdata(dctdata), .dctwren(dctwren));
	
	dct2d DCT(.clk(clock), .reset_n(reset_n), .rdy(dctrdy), .en(dcten), .iaddr(iaddr),
	.iq(iq), .maddr(dctmaddr), .mq(intra ? mq : nq), .waddr(dctwaddr), .wdata(dctwdata), .wwren(wwren));
	
	idct2d IDCT(.clk(clock), .reset_n(reset_n), .rdy(idctrdy), .en(idcten),
	.iaddr(dctwaddr), .idata(dctwdata), .iwren(wwren), .mq(intra ? imq : inq),
	.waddr(idctaddr), .wdata(dctdata), .wwren(dctwren));

	register #(32) SDRAM_BASE(.clk(clock), .in(reset_n ? ((mbase == SDRAM_BASE_1) ? SDRAM_BASE_2 : SDRAM_BASE_1) : 32'b0), .out(mbase), .en(switch || (reset_n == 1'b0)));
	register #(32) B1_BASE(.clk(clock), .in(reset_n ? dataIn : 32'b0), .out(b1_base), .en((wr_en == 1'b1) && (addr == 9'd1)));
	register #(32) B1_END(.clk(clock), .in(reset_n ? dataIn : 32'b0), .out(b1_end), .en((wr_en == 1'b1) && (addr == 9'd2)));
	register #(32) B2_BASE(.clk(clock), .in(reset_n ? dataIn : 32'b0), .out(b2_base), .en((wr_en == 1'b1) && (addr == 9'd3)));
	register #(32) B2_END(.clk(clock), .in(reset_n ? dataIn : 32'b0), .out(b2_end), .en((wr_en == 1'b1) && (addr == 9'd4)));
	register #(10) IX(.clk(clock), .in(reset_n ? next_ix : 10'b0), .out(ix), .en(1'b1));
	register #(10) IY(.clk(clock), .in(reset_n ? next_iy : 10'b0), .out(iy), .en(1'b1));
	register #(3)  BLOCK(.clk(clock), .in(reset_n ? next_block : 3'b0), .out(block), .en(1'b1));
	register #(1)  INTRA(.clk(clock), .in(reset_n ? next_intra : 1'b0), .out(intra), .en(1'b1));
	register #(1)  BUFSEL(.clk(clock), .in(reset_n ? !blksel : 1'b0), .out(blksel), .en(swap || (reset_n == 1'b0)));

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
		dataOut = 32'b0;
		next_state = state;
		switch = 1'b0;
		swap = 1'b0;
		m_wb = 1'b0;
		next_ix = ix;
		next_iy = iy;
		next_intra = intra;
		next_block = block;
		if (rd_en == 1'b1) begin
			if (addr == 8'b0) begin
				dataOut = {29'b0, buf2, buf1, state == 4'h0};
			end
		end
		case (state)
			4'h0: begin
				if (en) begin
					next_state = 4'h1;
					next_intra = 1'b1;
					next_ix = 10'b0;
					next_iy = 10'b0;
				end
			end
			4'h1: begin // Start motion estimator
				if (mrdy) begin
					men = 1'b1;
					m_wb = 1'b0;
					next_state = 4'h2;
					next_block = 3'b0;
				end
			end
			4'h2: begin // Start DCT
				if (mrdy && dctrdy) begin
					dcten = 1'b1;
					dsel = 1'b1;
					next_state = 4'h3;
				end
			end
			4'h3: begin // End DCT
				dsel = 1'b1;
				if (dctrdy) begin
					next_state = 4'h4;
				end
			end
			4'h4: begin // Start IDCT
				if (idctrdy) begin
					idcten = 1'b1;
					next_state = 4'h5;
				end
			end
			4'h5: begin // End IDCT
				if (idctrdy) begin
					next_block = block + 3'd1;
					if (block == 3'd5) begin
						next_state = 4'h6;
					end else begin
						next_state = 4'h2;
					end
				end
			end
			default: begin
				next_state = 4'h0;
			end
		endcase
	end

endmodule
