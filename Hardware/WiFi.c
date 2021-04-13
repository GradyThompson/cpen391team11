#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/mman.h>
#include <signal.h>

#define HW_REGS_BASE ( 0xff200000 )
#define HW_REGS_SPAN ( 0x00200000 )
#define HW_REGS_MASK ( HW_REGS_SPAN - 1 )
#define LED_PIO_BASE 0x30
#define PIR_PIO_BASE 0x70

volatile sig_atomic_t stop;

void catchSIGINT(int signum){
    stop = 1;
}

int main(void)
{
    volatile unsigned int *h2p_lw_led_addr=NULL;
    volatile unsigned int *h2p_lw_pir_addr=NULL;
    void *virtual_base;
        int fd;

    // catch SIGINT from ctrl+c, instead of having it abruptly close this progra
m
    signal(SIGINT, catchSIGINT);

    // Open /dev/mem
    if( ( fd = open( "/dev/mem", ( O_RDWR | O_SYNC ) ) ) == -1 ) {
        printf( "ERROR: could not open \"/dev/mem\"...\n" );
        return( 1 );
    }

    // get virtual addr that maps to physical
    virtual_base = mmap( NULL, HW_REGS_SPAN, ( PROT_READ | PROT_WRITE ), MAP_SHA
RED, fd, HW_REGS_BASE );
    if( virtual_base == MAP_FAILED ) {
        printf( "ERROR: mmap() failed...\n" );
        close( fd );
        return(1);
    }

    // Get the address that maps to the LEDs
    h2p_lw_led_addr=(unsigned int *)(virtual_base + (( LED_PIO_BASE ) & ( HW_REG
S_MASK ) ));
    h2p_lw_pir_addr=(unsigned int *)(virtual_base + (( PIR_PIO_BASE ) & ( HW_REG
S_MASK ) ));

    printf("Running leds. To exit, press Ctrl+C.\n");
    while(!stop){
      if (*h2p_lw_pir_addr == 1) {
	system("curl -X POST -F "video=@path" -d "date:2021-MM-DD HH:MM:SS" -d "length:HH:MM:SS" http://35.239.13.217:3000/uploadvideo");
      }
        *h2p_lw_led_addr = *h2p_lw_pir_addr;
    }

    if( munmap( virtual_base, HW_REGS_SPAN ) != 0 ) {
        printf( "ERROR: munmap() failed...\n" );
        close( fd );
        return( 1 );

    }
    close( fd );
    return 0;
}
