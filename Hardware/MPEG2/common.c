#include <mpeg2/common.h>
#include <mpeg2/bitstream.h>

void mpeg2_start_code(struct bitstream* bs, enum start_code code) {
	bs_align(bs, 8);
	bs_add(bs, 1, 24);
	bs_add(bs, (uint32_t)code, 8);
}

void mpeg2_marker_bit(struct bitstream* bs) {
	bs_add(bs, 1, 1);
}

void mpeg2_bytes(struct bitstream* bs, uint8_t* data, uint32_t size) {
	for (uint8_t i = 0; i < size; i++) {
		bs_add(bs, data[i], 8);
	}
}
