module camera (
		input [9:0] port_data,
		input port_clk,
		input port_horizontal_synch,
		input port_vertical_synch,
		input clk,
		
		output chip_select,
		output reference_clk,
		output I2C_clk,
		output I2C_data,
		output memory_address,
		output data_out,
		output write
	);
	
	
end module

module YUV_encoder (
		input [255:0] pixel data,
		
		output Y [7:0][7:0],
		output U [7:0][7:0],
		output V [7:0][7:0],
	);
	
end module
