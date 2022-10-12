cmake_minimum_required(VERSION 3.22)
project(java_launch_wrapper)

set(CMAKE_CXX_STANDARD 14)

include_directories(build/generated/sources/headers/java/main)
include_directories(src/main/cpp)

include_directories("C:/Program Files/Java/jdk1.8.0_333/include")
include_directories("C:/Program Files/Java/jdk1.8.0_333/include/win32")

add_library(
        wrapper
        SHARED
        build/generated/sources/headers/java/main/oolloo_jlw_ArgLoader.h
        src/main/cpp/ArgLoader.cpp
)

set_target_properties(
        wrapper
        PROPERTIES
        RUNTIME_OUTPUT_DIRECTORY
        ../src/main/resources
)