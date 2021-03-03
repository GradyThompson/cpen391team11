
module SerialIODecoder (
		input unsigned [15:0] Address,
		input IOSelect_H,
		input ByteSelect_L,
		
		output reg Bluetooth_Port_Enable,
		output reg Wifi_Port_Enable
	);

	always@(Address, IOSelect_H, ByteSelect_L) begin
		
// default values for the IO chip enables (default = disabled)
// they are overridden below when necessary - default values for outputs avoids inferring latches in VHDL 
// so we must do it for all our outputs
		
		Bluetooth_Port_Enable <= 0 ;
		Wifi_Port_Enable <= 0 ;
		
// IOSelect_H is driven logic 1 whenever the CPU outputs an address in the range A31:A0 = hex [FF21_0000] to [FF21_FFFF]
// that is, IOSelect_H is driven to logic 1 for all addresses in range [FF21_XXXX]. 
// All we have to do for each UART IO chip is decode the XXXX i.e. A15:A0 into 4 blocks of 16 bytes each. 
// All addresses of registers in our UART chips should be even address as they are byte wide and connected to 
// the upper half of the data bus (ByteSelect_L is asserted for an even byte transfer of D15-D8)

// decoder for the 1st UART 16550 chip (Bluetooth Port) - Registers located between addresses 0xFF21 0220 - 0xFF21 022F 
// so that they occupy same half of data bus on D15-D8 and ByteSelect_L = 0
			if((IOSelect_H == 1) && (Address[15:4] == 12'h022) && ByteSelect_L == 0)
				Bluetooth_Port_Enable <= 1 ;
				

// decoder for the 2nd UART 16550 chip (Wifi) - Registers located between addresses 0xFF21 0240 - 0xFF21 024F 
// so that they occupy same half of data bus on D15-D8 and ByteSelect_L = 0
			if((IOSelect_H == 1) && (Address[15:4] == 12'h024) && ByteSelect_L == 0)
				Wifi_Port_Enable <= 1 ;
				
	end
endmodule
