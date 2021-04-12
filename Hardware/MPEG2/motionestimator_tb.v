module motionestimator_tb();
	reg clk, rst, en, writeback, intra, waitrequest, readdatavalid, dctwren;
	wire rdy, read, write;
	reg  [9:0] ix, iy;
	reg  [31:0] base; // Avalon base address, for reading and writing
	// Avalon Master
	wire [31:0] addr;
	wire [7:0] wdata;
	reg  [7:0] rdata, raddr, dctaddr;
	// Camera Interface
	wire [9:0] x, y, xmin, ymin;
	wire [1:0] cc;
	reg  [7:0] px;
	// Output Interface
	wire [15:0] q;
	reg [15:0] dctdata;

	motionestimator DUT(.clk(clk), .reset_n(rst), .rdy(rdy), .en(en), .writeback(wb),
		.ix(ix), .iy(iy), .base(32'd12345678), .intra(intra), .read(rd), .write(wr),
		.addr(addr), .writedata(wdata), .readdata(rdata), .readdatavalid(valid),
		.waitrequest(await), .x(x), .y(y), .cc(cc), .px(px), .xmin(xmin), .ymin(ymin),
		.raddr(raddr), .q(q), .dctaddr(dctaddr), .dctdata(dctdata), .dctwren(dctwren)
	);

endmodule
