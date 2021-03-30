module Camera (
		//////////// CAMERA //////////
		output 		          		CAMERA_I2C_SCL,
		inout 		          		CAMERA_I2C_SDA,
		output		          		CAMERA_PWDN_n,
		output		          		MIPI_CS_n,
		inout 		          		MIPI_I2C_SCL,
		inout 		          		MIPI_I2C_SDA,
		output		          		MIPI_MCLK,
		input 		          		MIPI_PIXEL_CLK,
		input 		     [9:0]		MIPI_PIXEL_D,
		input 		          		MIPI_PIXEL_HS,
		input 		          		MIPI_PIXEL_VS,
		output		          		MIPI_REFCLK,
		output		          		MIPI_RESET_n,
		input RESET_N,
		
		//////////// CLOCK //////////
		input 		          		CLOCK2_50,
		input 		          		CLOCK3_50,
	
		//////////// SDRAM //////////
		output [63:0] data,
		output [15:0] wraddress,
		output wren
	);
	
	//=============================================================================
	// REG/WIRE declarations
	//=============================================================================


	wire [7:0] RED   ; 
	wire [7:0] GREEN  ; 
	wire [7:0] BLUE 		 ; 

	wire        I2C_RELEASE ;  
	wire        CAMERA_I2C_SCL_MIPI ; 
	wire        CAMERA_I2C_SCL_AF;
	wire        CAMERA_MIPI_RELAESE ;
	wire        MIPI_BRIDGE_RELEASE ;  
	wire READ_Request;
	
	reg [9:0] row, col;
	wire [7:0] Y, U, V;
	reg [63:0] Ys, Us, Vs;
	wire Y_ready, U_ready, V_ready;
	
	//=======================================================
	// Structural coding
	//=======================================================

	assign READ_Request = 1'b1;
	
	//------ MIPI BRIGE & CAMERA RESET  --
	assign CAMERA_PWDN_n  = 1; 
	assign MIPI_CS_n      = 0; 
	assign MIPI_RESET_n   = RESET_N ;

	//------ CAMERA MODULE I2C SWITCH  --
	assign I2C_RELEASE    = CAMERA_MIPI_RELAESE & MIPI_BRIDGE_RELEASE; 
	assign CAMERA_I2C_SCL =( I2C_RELEASE  )?  CAMERA_I2C_SCL_AF  : CAMERA_I2C_SCL_MIPI ;   
	 
	//------ MIPI BRIGE & CAMERA SETTING  --  
	MIPI_BRIDGE_CAMERA_Config    cfin(
								 .RESET_N           ( RESET_N ), 
								 .CLK_50            ( CLOCK2_50 ), 
								 .MIPI_I2C_SCL      ( MIPI_I2C_SCL ), 
								 .MIPI_I2C_SDA      ( MIPI_I2C_SDA ), 
								 .MIPI_I2C_RELEASE  ( MIPI_BRIDGE_RELEASE ),  
								 .CAMERA_I2C_SCL    ( CAMERA_I2C_SCL_MIPI ),
								 .CAMERA_I2C_SDA    ( CAMERA_I2C_SDA ),
								 .CAMERA_I2C_RELAESE( CAMERA_MIPI_RELAESE )
					 );
		 
	//------ CMOS CCD_DATA TO RGB_DATA -- 
	
	RAW2RGB_J				u4	(	
								.RST          ( MIPI_PIXEL_VS ),
								.iDATA        ( MIPI_PIXEL_D ),

								//-----------------------------------
								.VGA_CLK      ( MIPI_PIXEL_CLK ),
								.READ_Request ( READ_Request ),
								.VGA_VS       ( MIPI_PIXEL_VS ),	
								.VGA_HS       ( MIPI_PIXEL_HS ) , 
											
								.oRed         ( RED  ),
								.oGreen       ( GREEN),
								.oBlue        ( BLUE )


								);
								
	assign Y_ready = col[2:0] == 3'b111;
	assign U_ready = (row[0] == 1'b0) && (col[3:0] == 4'b1110);
	assign V_ready = (row[0] == 1'b1) && (col[3:0] == 4'b1110);
								
	assign wren = Y_ready || U_ready || V_ready;
	
	assign data = ({64{Y_ready}} & Ys) | ({64{U_ready}} & Us) | ({64{V_ready}} & Vs);
	
	assign wraddress = ({16{Y_ready}} & ((16'd640*row + col)>>6)) | 
							({16{U_ready}} & ((16'd640*16'd480 + 16'd640*row + col)>>6)) | 
							({16{V_ready}} & ((16'd640*16'd480 + 16'd640*16'd120 + 16'd640*row + col)>>6));
						
	RGB2YUV rgb_to_yuv (RED, BLUE, GREEN, Y, U, V);
	
	always @(posedge MIPI_PIXEL_CLK) begin
		if (!RESET_N) begin
			row <= {10{1'b0}};
			col <= {10{1'b0}};
		end else if (MIPI_PIXEL_VS) begin
			row <= {10{1'b0}};
			col <= {10{1'b0}};
		end else if (MIPI_PIXEL_HS) begin
			row <= {10{1'b0}};
			col <= col + 1;
		end else begin
			row <= row + 1;
			col <= col;
		end
	end
	
	always @(row, col) begin
		if (row[0] == 1'b0) begin
			Ys <= Y << 64'd8*col[2:0];
			if (col[0] == 1'b0) begin
				Us <= U << 64'd8*col[3:1];
			end
		end else begin
			Ys <= Y << 64'd8*col[2:0];
			if (col[0] == 1'b0) begin
				Vs <= V << 64'd8*col[3:1];
			end
		end
	end
endmodule