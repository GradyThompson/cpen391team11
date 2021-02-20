#include <mpeg2/dct.h>
#include <string.h>
#include <math.h>
#include <stdio.h>
#define INDEX2D(ARR, X, Y) ARR[(Y * 8) + X]

void dct_preprocess(uint16_t* matrix) {
	const double scale[8] = {
		1 / sqrt(8), 1.0 / 2, 1.0 / 2, 1 / sqrt(2),
		1 / sqrt(8), 1.0 / 2, 1.0 / 2, 1 / sqrt(2)
	};
	
	for (uint8_t i = 0; i < 8; i++) {
		for (uint8_t j = 0; j < 8; j++) {
			// Scale row i by scale[i], column j by scale[j]
			INDEX2D(matrix, i, j) = (65536 * scale[i] * scale[j]) / INDEX2D(matrix, i, j);
		}
	}
}

void dct_1d(const int16_t* input, int8_t stride, int16_t* output) {
	int16_t stage1[8] = {};
	int16_t stage2[8] = {};
	
	// Stage 1 of Fig. 1
	for (uint8_t i = 0; i < 4; i++) {
		stage1[7-i] = input[(7-i) * stride] - input[i * stride];
		stage1[i]   = input[(7-i) * stride] + input[i * stride];
	}

	// Stage 2 of Fig. 1
	memcpy(stage2, stage1, 8 * sizeof(int16_t));
	stage2[3] = stage1[3] - stage1[0];
	stage2[0] = stage1[3] + stage1[0];
	stage2[2] = stage1[2] - stage1[1];
	stage2[1] = stage1[2] + stage1[1];

	// Stage 3 of Fig. 1
	output[0 * stride] = stage2[0] + stage2[1];
	output[1 * stride] = -(stage2[6] + stage2[7]);
	output[2 * stride] = -stage2[3];
	output[3 * stride] = stage2[5];
	output[4 * stride] = stage2[0] - stage2[1];
	output[5 * stride] = stage2[6] - stage2[7];
	output[6 * stride] = stage2[2];
	output[7 * stride] = stage2[4];
}

void dct_sq(const uint8_t* input, const uint16_t* matrix, int16_t* output) {
	int16_t RAM[64];
	
	// Shift from [0, 255] to [-128, 127]
	for (int8_t i = 0; i < 64; i++) {
		RAM[i] = input[i] - 128;
	}
	
	// Compute 1D DCT over rows
	for (int8_t i = 0; i < 8; i++) {
		dct_1d(RAM + (8*i), 1, RAM + (8 * i));
	}
	
	// Compute 1D DCT over columns
	for (int8_t i = 0; i < 8; i++) {
		dct_1d(RAM + i, 8, RAM + i);
	}
	
	// Apply quantization step
	for (int8_t i = 0; i < 64; i++) {
		output[i] = (RAM[i] * matrix[i]) / 65536;
	}
}
