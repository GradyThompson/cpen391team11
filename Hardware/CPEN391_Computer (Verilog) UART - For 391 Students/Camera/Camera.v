module Camera (
		input [9:0] MIPI_pixel_D,
		input MIPI_pixel_clk,
		input MIPI_pixel_HS,
		input MIPI_pixel_VS,
		input clk,
		input reset_n,
		
		output MIPI_reset,
		output MIPI_refclk,
		output MIPI_chip_select,
		output MIPI_I2C_scl,
		output MIPI_I2C_sda,
		output camera_pwdwn,
		output camera_I2C_scl,
		output camera_I2C_sda,
		output memory_address,
		output data_out,
		output write
	);
	
	
end module