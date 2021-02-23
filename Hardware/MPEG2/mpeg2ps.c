#include <mpeg2/mpeg2ps.h>
#include <mpeg2/bitstream.h>
void mpeg2_program_pack(struct bitstream* bs, uint32_t bitrate) {
	mpeg2_start_code(bs, SC_PACK);
	bs_add(bs, 0b01000, 5);
	mpeg2_marker_bit(bs);
	bs_add(bs, 0, 15);
	mpeg2_marker_bit(bs);
	bs_add(bs, 0, 15);
	mpeg2_marker_bit(bs);
	bs_add(bs, 0, 9);
	mpeg2_marker_bit(bs);
	// Bitrate is given in bits per second, this is in 50 bytes / second
	bs_add(bs, bitrate / 400, 22);
	mpeg2_marker_bit(bs);
	mpeg2_marker_bit(bs);
	bs_add(bs, 0xF8, 8);
}

void mpeg2_video_sequence(struct bitstream* bs, struct mpeg2_settings* hdr) {
//  Sequence header
	mpeg2_start_code(bs, SC_SEQ);
	bs_add(bs, hdr->h_size & 0xFFF, 12);
	bs_add(bs, hdr->v_size & 0xFFF, 12);
	bs_add(bs, hdr->ar, 4);
	bs_add(bs, hdr->fr, 4);
	bs_add(bs, hdr->bitrate & 0x3FFFF, 18);
	mpeg2_marker_bit(bs);
	bs_add(bs, hdr->vbv_size & 0x3FF, 10);
	bs_add(bs, 0, 1); // constrained_parameters_flag

	if (hdr->intra_qm) {
		mpeg2_marker_bit(bs); // load_intra_quantizer_matrix
		mpeg2_bytes(bs, hdr->intra_qm, 64);
	} else {
		bs_add(bs, 0, 1);
	}

	if (hdr->inter_qm) {
		mpeg2_marker_bit(bs); // load_intra_quantizer_matrix
		mpeg2_bytes(bs, hdr->inter_qm, 64);
	} else {
		bs_add(bs, 0, 1);
	}

//  Sequence extension
	mpeg2_start_code(bs, SC_EXT);
	bs_add(bs, EC_SEQ_EXT, 4);
	bs_add(bs, 0, 1);
	bs_add(bs, hdr->profile, 3);
	bs_add(bs, hdr->level, 4);
	// Only Progressive YUV420 is supported
	bs_add(bs, 1, 1);
	bs_add(bs, 1, 2);
	// High bits of horizontal and vertical size
	bs_add(bs, (hdr->h_size >> 12) & 0x03, 2);
	bs_add(bs, (hdr->v_size >> 12) & 0x03, 2);
	// High bits of bitrate
	bs_add(bs, (hdr->bitrate >> 18) & 0xFFF, 12);
	mpeg2_marker_bit(bs);
	// High bits of VBV size
	bs_add(bs, (hdr->vbv_size >> 10) & 0xFF, 8);
	// Not low delay
	bs_add(bs, 0, 1);
	// Frame rate multiplier: 1/1
	bs_add(bs, 0, 2);
	bs_add(bs, 0, 5);
}

void mpeg2_gop_header(struct bitstream* bs, uint32_t time_code) {
	mpeg2_start_code(bs, SC_GOP);
	bs_add(bs, time_code & 0x1FFFFFF, 25);
	// Only closed GOPs are supported
	bs_add(bs, 1, 1);
	bs_add(bs, 0, 1);
}

void mpeg2_picture_header(struct bitstream* bs, struct mpeg2_ph* ph) {
	mpeg2_start_code(bs, SC_PICTURE);
	bs_add(bs, ph->temporal_ref, 12);
	bs_add(bs, ph->type, 3);
	bs_add(bs, ph->vbv_delay, 16);

	// Unused MPEG-1 flags
	switch (ph->type) {
	case MT_B:
		bs_add(bs, 7, 4);
	case MT_P:
		bs_add(bs, 7, 4);
	default:
		break;
	}

	// extra_bit_picture, reserved
	bs_add(bs, 0, 1);
	
	mpeg2_start_code(bs, SC_EXT);
	bs_add(bs, EC_PIC_CODE, 4);
	// Ranges of possible prediction vectors, probably 2
	for (int8_t i = 0; i < 4; i++) {
		bs_add(bs, ph->f_code[i], 4);
	}
	bs_add(bs, ph->dc_precision, 2);
	bs_add(bs, 3, 2); // only progressive supported
	bs_add(bs, (ph->repeat & 2) >> 1, 1); // Supports decoding the same frame multiple times
	bs_add(bs, 1, 1); // frame_pred_frame_dct - only progressive, not interlaced
	bs_add(bs, 0, 1); // concealment_motion_vectors are not supported
	bs_add(bs, ph->q_scale_type, 1);
	bs_add(bs, ph->intra_vlc_format, 1);
	bs_add(bs, 0, 1); // alternate_scan - only progressive
	bs_add(bs, ph->repeat & 1, 1); // repeat_first_field
	// chroma_420_type, progressive_frame, and composite_display_flag
	// These have fixed values because only progressive video is supported,
	// and we are not capturing from analog video
	bs_add(bs, 6, 3);
}

void mpeg2_slice(struct bitstream* bs, uint8_t y_position, uint8_t scale_code) {
	mpeg2_start_code(bs, y_position);
	bs_add(bs, scale_code, 5);
	bs_add(bs, 0, 2); // slice_extension_flag and extra_bit_slice
}

void mpeg2_macroblock(struct bitstream* bs, uint8_t* data, int16_t last_dc) {
	mpeg2_marker_bit(bs); // macroblock_address_increment of 1
	mpeg2_marker_bit(bs); // I-picture, intra, not quant
//	if (macroblock_quant)
//		quantiser_scale_code
//	if (macroblock_motion_forward)
//		motion_vectors(0)
//	if (macroblock_motion_backward)
//		motion_vectors(1)
//	if (macroblock_pattern)
//		coded_block_pattern
//  blocks go here
}
