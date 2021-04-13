# cpen391team11
## The project
This project Smart i, is a smart security solution making use of a PIR sensor (infrared motion detection). The system records suspicious events, rating the severity or security threat and notifying the user. The camera records the events and is avaiable through the mobile app.

## Running the program
Required Hardware:
- D8M GPIO Camera from Terasic
- PIR sensor
- DE1 SOC
Required Software:
- Quartus


Hardware setup:
- Attach D8M GPIO to GPIO pins 1 of DE1 SOC
- Connect PIR sensor ground to pin 0 if GPIO 0 and power to pin 1 of the DE1 SOC, connect the output to pin 2


To run the project:
1. Download CPEN391_Computer (Verilog) UART - For 391 Students.zip
2. Unzip into Hardware folder
3. Clone into folder
4. Pull
5. Install DE1 SOC linux distribution onto an sd card
6. Copy the WiFi.c file onto the sd card
7. Convert the Quartus project into an rbf file
8. Copy the rbf file onto the sd card
9. Initialize the fpga to the project rbf (can be set to default)
10. Run WiFi.c on the DE1 SOC through linux
