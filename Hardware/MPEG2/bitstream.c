#include <mpeg2/bitstream.h>
#include <stdlib.h>
#include <string.h>

#define BS_BASE_SIZE 16

struct bitstream * bs_alloc(void) {
	struct bitstream* bs = calloc(0, sizeof(struct bitstream));

	bs->data = calloc(0, BS_BASE_SIZE * sizeof(uint32_t));
	bs->capacity = 16;
	
	return bs;
}

void bs_free(struct bitstream* bs) {
	free(bs->data);
	free(bs);
}

void bs_align(struct bitstream* bs, uint8_t bits) {
	uint32_t total_bits = bs->length * 8 + bs->part_length;
	uint32_t new_bits   = (total_bits % bits) ? bits - (total_bits % bits) : 0;
	
	while (new_bits >= 32) {
		bs_add(bs, 0, 32);
		new_bits -= 32;
	}
	
	bs_add(bs, 0, new_bits);
}

static void bs_push(struct bitstream* bs) {
	if (bs->length == bs->capacity) {
		uint8_t* new_data = calloc(0, bs->capacity * 2);
		memcpy(new_data, bs->data, bs->capacity);
		bs->data = new_data;
		bs->capacity = 2 * bs->capacity;
	}
	
	bs->data[bs->length] = bs->part;
	bs->length++;
	bs->part = 0;
	bs->part_length = 0;
}

void bs_add(struct bitstream* bs, uint32_t data, uint8_t length) {
	if ((length + bs->part_length) >= 8) {
		uint8_t  part_len  = (8 - bs->part_length);
		uint32_t part_bits = (data >> (length - part_len));
		
		bs->part = (bs->part << part_len) | (part_bits);
		data = data & (uint32_t)((1 << (length - part_len)) - 1);
		length  -= part_len;
		bs_push(bs);
	}
	
	while (length >= 8) {
		bs->part = data >> (length - 8);
		bs_push(bs);
		data = data & (uint32_t)((1 << (length - 8)) - 1);
		length -= 8;
	}
	
	bs->part = (bs->part << length) | data;
	bs->part_length += length;
}
