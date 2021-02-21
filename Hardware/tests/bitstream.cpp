#include <catch2/catch.hpp>
#include <mpeg2/bitstream.h>

TEST_CASE("Basic bitstream test", "[bitstream]") {
	bitstream* bs = bs_alloc();
	
	bs_add(bs, 0x12, 8);

	REQUIRE(bs->data[0]     == 0x12);
	REQUIRE(bs->length      == 1);
	REQUIRE(bs->part        == 0);
	REQUIRE(bs->part_length == 0);

	bs_free(bs);
}

TEST_CASE("Endianness test", "[bitstream]") {
	bitstream* bs = bs_alloc();
	
	bs_add(bs, 0x12345678, 32);

	REQUIRE(bs->data[0] == 0x12);
	REQUIRE(bs->data[1] == 0x34);
	REQUIRE(bs->data[2] == 0x56);
	REQUIRE(bs->data[3] == 0x78);
	REQUIRE(bs->length  == 4);

	bs_free(bs);
}

TEST_CASE("Multiple add test", "[bitstream]") {
	bitstream* bs = bs_alloc();
	
	bs_add(bs, 0x123, 12);
	bs_add(bs, 0x2, 3);
	bs_add(bs, 0x5678, 17);

	REQUIRE(bs->data[0] == 0x12);
	REQUIRE(bs->data[1] == 0x34);
	REQUIRE(bs->data[2] == 0x56);
	REQUIRE(bs->data[3] == 0x78);
	REQUIRE(bs->length  == 4);

	bs_free(bs);
}

TEST_CASE("Align test", "[bitstream]") {
	bitstream* bs = bs_alloc();

	bs_add(bs, 0x123, 11);
	bs_align(bs, 8);
	
	REQUIRE(bs->data[0] == 0x24);
	REQUIRE(bs->data[1] == 0x60);
	REQUIRE(bs->length  == 2);
	
	bs_free(bs);
}
