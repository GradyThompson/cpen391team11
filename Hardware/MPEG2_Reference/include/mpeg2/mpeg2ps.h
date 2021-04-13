#ifndef MPEG2_PS_H
#define MPEG2_PS_H

#include <mpeg2/common.h>
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

enum aspect_ratio {
	AR_1_1  = 1,
	AR_4_3  = 2,
	AR_16_9 = 3,
	AR_221  = 4
};

enum frame_rate {
	FR_23976 = 1,
	FR_24    = 2,
	FR_25    = 3,
	FR_2997  = 4,
	FR_30    = 5,
	FR_50    = 6,
	FR_5994  = 7,
	FR_60    = 8
};

enum mpeg2_profile {
	MP_HIGH = 1,
	MP_SPAS = 2,
	MP_SNRS = 3,
	MP_MAIN = 4,
	MP_SIMP = 5
};

enum mpeg2_level {
	ML_HIGH = 4,
	ML_1440 = 6,
	ML_MAIN = 8,
	ML_LOW  = 10
};

enum mpeg2_ptype {
	MT_I = 1,
	MT_P = 2,
	MT_B = 3
};

enum intra_dc_pre {
	IDP_8  = 0,
	IDP_9  = 1,
	IDP_10 = 2,
	IDP_11 = 3
};

enum repeat_frames {
	RF_ONCE   = 0,
	RF_TWICE  = 1,
	RF_THRICE = 3
};

struct mpeg2_settings {
	uint32_t h_size;
	uint32_t v_size;
	enum aspect_ratio ar;
	enum frame_rate   fr;
	enum mpeg2_ptype  profile;
	enum mpeg2_level  level;
	uint32_t bitrate;
	uint32_t vbv_size;
	uint8_t* intra_qm;
	uint8_t* inter_qm;
};

struct mpeg2_ph {
	uint16_t temporal_ref;
	enum mpeg2_ptype type;
	uint16_t vbv_delay;
	// For hardware encode, set f_code[0] and f_code[2] to 2, f_code[1] and f_code[3] to 3
	uint8_t  f_code[4];
	enum intra_dc_pre dc_precision; // precision of DC coefficient
	enum repeat_frames repeat; // number of times to repeat this frame
	bool     q_scale_type;     // false if using linear QS table, true if using new QS table (table 7-6)
	bool     intra_vlc_format; // false if using B.14 table, true if using B.15 table
};

void mpeg2_program_pack(struct bitstream* bs, uint32_t bitrate);
void mpeg2_video_sequence(struct bitstream* bs, struct mpeg2_settings* hdr);
void mpeg2_gop_header(struct bitstream* bs, uint32_t time_code);
void mpeg2_picture_header(struct bitstream* bs, struct mpeg2_ph* ph);
void mpeg2_slice(struct bitstream* bs, uint8_t y_position, uint8_t scale_code);
void mpeg2_macroblock(struct bitstream* bs, uint8_t* data, int16_t last_dc);

#ifdef __cplusplus
}
#endif

#endif
