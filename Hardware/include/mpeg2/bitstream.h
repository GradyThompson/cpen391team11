#ifndef BITSTREAM_H
#define BITSTREAM_H

#include <stdint.h>
#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

struct bitstream {
	FILE*    file;
	uint8_t* data;
	uint32_t capacity;
	uint32_t length;
	uint8_t  part;
	uint8_t  part_length;
};

// Allocate new bitstream
struct bitstream * bs_alloc(FILE* file);
// Free bitstream and data. Does not close the file
void bs_free(struct bitstream* bs);
// Byte align and flush to file
void bs_flush(struct bitstream* bs);
// Align bitstream to length of bits bits
void bs_align(struct bitstream* bs, uint8_t bits);
// Length cannot be more than 32
void bs_add(struct bitstream* bs, uint32_t data, uint8_t length);

#ifdef __cplusplus
}
#endif

#endif
