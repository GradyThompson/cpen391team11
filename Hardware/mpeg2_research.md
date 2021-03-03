# MPEG-2 Research & Design

### Things to cover in meeting (2/23)

- Resolution and Frame rate
  - Resolution must be a multiple of 16 in both dimensions
  - Frame rates of 24, 30, 50, 60
  - I was thinking 1280x720 @ 30 FPS
- Bitrate and other configurable options
  - We need to measure the write speed we can rely on for SD cards
- Interfaces
  - Configuration, both basic and advanced
    - Accesible from the DE1, maybe accesible from the app?
  - DMA to RAM that's accesible in software
    - 2-buffer solution where buffers swap when one is full?
  - Buffer to store frames in
    - Need either 1 or 2 depending on how complex the encoder becomes
  - Pixels from the camera
    - MPEG-2 expects it divided into 16x16 macroblocks, in the Y'CbCr 4:2:0 format
- What to do for M2 vs M3

### Design

Most of the high-level design is covered by [this](http://www.bretl.com/mpeghtml/codecdia1.HTM),
except for the RAW - YUV conversion which is done in hardware by the camera,
and packaging the output into a playable video file.

#### MPEG-2

There are 3 types of frames:
- I frames are just JPEGs but encoded slightly differently
- P frames encode the difference from earlier frames
- B frames encode a point in between a single past frame and a single future frame
- I frames take the most space, then P frames, then B frames

Tasks
- I frame-only video needs:
  - Y'CbCr / YUV 4:2:0 pixels as input
  - DCT / Discrete Cosine Transform
  - Quantization
  - Variable Length Coder
  - Packaging video into a playable format
- On top of that, P frames need
  - Enough embedded memory to store 1 uncompressed frame in YUV 4:2:0
  - Motion estimation
  - Inverse Quantization & IDCT, which is covered by the DCT/Quant implementation
  - Extensions to the Variable Length Coder and packaging subsystems
- On top of that, B frames need
  - Enough embedded memory to store a second uncompressed frame in YUV 4:2:0
  - Some kind of interpolation

Things for M2
- Interface design
- Document this low-level design
- Possibly an I-only or I/P demo?

#### Existing Design Work

The DCT and Motion Estimation steps are the most important part to accelerate:

- Approximate DCT from https://doi.org/10.1007/s11042-019-08325-2 is what I chose
- Quantization design is custom, but it's partially from the DCT paper
- Motion Estimation uses a [Block Matching Algorithm](https://en.wikipedia.org/wiki/Block-matching_algorithm), a good choice is NTSS
- MPEG-2 supports [Half-Pixel Motion Estimation](https://doi-org.ezproxy.library.ubc.ca/10.1007/978-0-387-78414-4_116) which is something we can add on later

ZigZag, RLE, Huffman Coding

- After the DCT and Quantization, there's a highly serial coding step
- I'm unsure whether this should be done in hardware or software

Converting the output into a playable video file

- There are different formats we can use
  - MPEG-2 specifies the Program Stream (PS) for writing to disk and the Transport Stream (TS) for transmitting over e.g. WiFi
  - Matroska/MKV is another option
- No matter what container we choose, we'll need to create a Video Sequence
  - My initial design is in mpeg2ps.h / mpeg2ps.c

### References

#### Architecture

Encoder architecture: http://www.bretl.com/mpeghtml/codecdia1.HTM

Approximate DCT: https://doi.org/10.1007/s11042-019-08325-2

NTSS Block-Matching: A New Three-Step Search Algorithm for Block Motion Estimation, Renxiang Li, Bing Zeng, and Ming L. Liou, IEEE

Half-Pixel Motion Estimation: https://doi-org.ezproxy.library.ubc.ca/10.1007/978-0-387-78414-4_116

#### MPEG-2

MPEG-2 Overview: https://www2.seas.gwu.edu/~ayoussef/cs6351/standards.html#mpeg

More MPEG-2 Overview: http://www.co-bw.com/MultiMedia%20Articles/MPEG%20Compression%20C7.pdf

MPEG-2 Standard: ITU-T Rec. H.262 (2000 E), can be found online

MPEG-2 PS/TS:  ITU-T Rec. H.222.0, can be found online

Graphical MPEG-2 data layout: http://andrewduncan.net/mpeg/mpeg-2.html

MPEG-2 Headers Quick Reference: http://dvdnav.mplayerhq.hu/dvdinfo/mpeghdrs.html

Explanation of how MPEG-2 works: http://www.cs.columbia.edu/~delbert/docs/Dueck%20--%20MPEG-2%20Video%20Transcoding.pdf
