#ifndef DCT_H
#define DCT_H

#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

// Preprocess Quantization Matrix for DCT
void dct_preprocess(uint16_t* matrix);

// 1-D DCT approximation from https://doi.org/10.1007/s11042-019-08325-2
void dct_1d(const int16_t* input, int8_t stride, int16_t* output);

// 2-D DCT approximation and quantization
void dct_sq(const uint8_t* input, const uint16_t* matrix, int16_t* output);

#ifdef __cplusplus
}
#endif

#endif
