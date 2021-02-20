CC  = gcc
CXX = g++
INC_FLAGS := -I include

CPPFLAGS = $(INC_FLAGS) -MT $@ -MMD -MP -MF build/$*.d
CFLAGS   = -Wall -O2
CXXFLAGS = -Wall -O2 -std=c++17

TEST_SRCS   := $(shell find tests -path "*.cpp") $(shell find MPEG2 -path "*.cpp") $(shell find MPEG2 -path "*.c")
TEST_OBJS   := $(patsubst %.c, ./build/%.o, $(patsubst %.cpp, ./build/%.o, $(TEST_SRCS)))
TEST_DEPS   := $(TEST_OBJS:.o=.d)
TEST_TARGET := catchmain

all:


test: $(TEST_OBJS)
	$(CXX) -o $(TEST_TARGET) $(TEST_OBJS)
	./$(TEST_TARGET)
	rm $(TEST_TARGET)

./build/%.o : ./%.cpp
	mkdir -p $(dir $@)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c -o $@ $<

./build/%.o : ./%.c
	mkdir -p $(dir $@)
	$(CC) $(CPPFLAGS) $(CFLAGS) -c -o $@ $<

clean:
	rm -rf $(TEST_TARGET) build/

-include $(TEST_DEPS)
