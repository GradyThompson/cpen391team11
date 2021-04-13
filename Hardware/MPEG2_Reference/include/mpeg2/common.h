#ifndef MPEG2_COMMON_H
#define MPEG2_COMMON_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

struct bitstream;
enum start_code {
	SC_PICTURE  = 0x00,
	SC_SLICE    = 0x01,
	SC_USERDATA = 0xB2,
	SC_SEQ      = 0xB3,
	SC_SERROR   = 0xB4,
	SC_EXT      = 0xB5,
	SC_SEQEND   = 0xB7,
	SC_GOP      = 0xB8,
	SC_PACK     = 0xBA
};

enum ext_code {
	EC_SEQ_EXT   = 1,
	EC_SEQ_DIS   = 2,
	EC_QUANT     = 3,
	EC_COPYRIGHT = 4,
	EC_SEQ_SCALE = 5,
	EC_PIC_DISP  = 7,
	EC_PIC_CODE  = 8,
	EC_SPA_SCALE = 9,
	EC_TEM_SCALE = 10
};

void mpeg2_start_code(struct bitstream* bs, enum start_code code);
void mpeg2_marker_bit(struct bitstream* bs);
void mpeg2_bytes(struct bitstream* bs, uint8_t* data, uint32_t size);

#ifdef __cplusplus
}
#endif

#endif
