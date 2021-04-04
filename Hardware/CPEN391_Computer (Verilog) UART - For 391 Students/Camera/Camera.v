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
	
		//////////// FRAME BUFFER 1 //////////
		output [39:0] data1,
		output [15:0] wraddress1,
		output wren1,
		
		//////////// FRAME BUFFER 1 //////////
		output [143:0] data2,
		output [12:0] wraddress2,
		output wren2
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
	reg [39:0] Ys;
	reg [143:0] Us, Vs;
	wire Y_ready, U1_ready, U2_ready, V_ready;
	
	wire [15:0] Y_addr;
	wire [5:0] Y_data_pos;
	
	wire [15:0] U_addr;
	wire U_frame_buffer_num;
	wire [7:0] U_data_pos;
	
	wire [12:0] V_addr;
	wire [7:0] V_data_pos;
	
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
								
	RGB2YUV rgb_to_yuv (RED, BLUE, GREEN, Y, U, V);
	
	/*
		Writes when the data position is at the end (therefore when all data is available)
		Y only writes on even columns
		U only writes on odd columns and rows
		V only writes on odd columns and even rows
	*/
	assign Y_ready = Y_data_pos == 6'd32;
	assign U1_ready = ((U_data_pos == 6'd32) && !U_frame_buffer_num) && (col[0] == 1'b0) && (row[0] == 1'b0);
	assign U2_ready = ((U_data_pos == 8'd136) && U_frame_buffer_num) && (col[0] == 1'b0) && (row[0] == 1'b0);
	assign V_ready = (V_data_pos == 8'd136) && (col[0] == 1'b0) && (row[0] == 1'b1);
								
	assign wren1 = Y_ready || U1_ready;
	assign data1 = ({40{Y_ready}} & Ys) | ({40{U1_ready}} & Us);
	assign wraddress1 = ({40{Y_ready}} & Y_addr) | ({40{U1_ready}} & U_addr);
		
		
	assign wren2 = U2_ready || V_ready;
	assign data2 = ({144{U2_ready}} & Us) | ({144{V_ready}} & Vs);
	assign wraddress2 = ({144{U2_ready}} & U_addr) | ({144{V_ready}} & V_addr);
	
	XY2ADDRESS xy_to_addr(	row, col, 2'b00, U_frame_buffer_num,
									Y_addr, Y_data_pos, U_addr, U_data_pos, V_addr, V_data_pos);
		
	wire [15:0] Y_addr;
	wire Y_frame_buffer_num;
	wire [7:0] Y_data_pos;
	
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
	
	always @(Y_data_pos) begin
		if (Y_data_pos == 6'd0) begin
			Ys = {40{1'b0}};
		end
		Ys <= Ys | ((40'd1*Y) << Y_data_pos);
	end
	
	always @(U_data_pos) begin
		if ((col[0] == 1'b0) && (row[0] == 1'b0)) begin
			if (U_data_pos == 7'd0) begin
				Us = {144{1'b0}};
			end
			Us <= Us | ((144'd1*U) << U_data_pos);
		end
	end
	
	always @(V_data_pos) begin
		if ((col[0] == 1'b0) && (row[0] == 1'b1)) begin
			if (Y_data_pos == 7'd0) begin
				Vs = {144{1'b0}};
			end
			Vs <= Vs | ((144'd1*V) << V_data_pos);
		end
	end
endmodule