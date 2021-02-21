#ifndef BITSTREAM_H
#define BITSTREAM_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

struct bitstream {
	uint8_t* data;
	uint32_t capacity;
	uint32_t length;
	uint8_t  part;
	uint8_t  part_length;
};

// Allocate new bitstream
struct bitstream * bs_alloc(void);
// Free bitstream and data
void bs_free(struct bitstream* bs);
// Align bitstream to length of bits bits
void bs_align(struct bitstream* bs, uint8_t bits);
// Length cannot be more than 32
void bs_add(struct bitstream* bs, uint32_t data, uint8_t length);

#ifdef __cplusplus
}
#endif

#endif
