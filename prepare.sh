#!/bin/bash

set -e

DYN_LIB_FILE_NAME=libluajit.so
STATIC_LIB_FILE_NAME=libluajit.a
LUAJIT_DIR=luajit

OUT_DIR=luajit-dist
OUT_DIR_LIB=$OUT_DIR/lib
OUT_INCLUDE_DIR=$OUT_DIR/include

JNI_SOURCES_DIR=jni-sources
JNI_SOURCES_INC_DIR=$JNI_SOURCES_DIR/include
JNI_SOURCES_LIB_DIR=$JNI_SOURCES_DIR/libs

LUAJAVA_DIR=luajava
LUAJAVA_JAVA_DIR=$LUAJAVA_DIR/src/java
LUAJAVA_C_DIR=$LUAJAVA_DIR/src/c

SPOT_JAVA_DIR=splot/src/main/java
SPLOT_JNI_LIBS_DIR=splot/src/main/jniLibs

rm -rf $OUT_DIR
mkdir -p $OUT_DIR_LIB
mkdir -p $OUT_INCLUDE_DIR

# Common settings
NDK=$HOME/android-ndk-r10
NDKABI=19

# armeabi
echo "Building armeabi.."
make -C "${LUAJIT_DIR}" clean

NDKVER=$NDK/toolchains/arm-linux-androideabi-4.6
NDKP=$NDKVER/prebuilt/linux-x86/bin/arm-linux-androideabi-
NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-arm"
make -C "${LUAJIT_DIR}" HOST_CC="gcc -m32" CROSS=$NDKP TARGET_FLAGS="$NDKF"
[ $? -ne 0 ] && exit -1

mkdir -p $OUT_DIR_LIB/armeabi
mv "${LUAJIT_DIR}/src/${DYN_LIB_FILE_NAME}" "${OUT_DIR_LIB}/armeabi/${DYN_LIB_FILE_NAME}"
mv "${LUAJIT_DIR}/src/${STATIC_LIB_FILE_NAME}" "${OUT_DIR_LIB}/armeabi/${STATIC_LIB_FILE_NAME}"

# armeabi-v7a
echo -e "\n\nBuilding armeabi-v7a.."
make -C "${LUAJIT_DIR}" clean
NDKVER=$NDK/toolchains/arm-linux-androideabi-4.6
NDKP=$NDKVER/prebuilt/linux-x86/bin/arm-linux-androideabi-
NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-arm"
NDKARCH="-march=armv7-a -mfloat-abi=softfp -Wl,--fix-cortex-a8"
make -C "${LUAJIT_DIR}" CROSS=$NDKP TARGET_FLAGS="$NDKF $NDKARCH"
[ $? -ne 0 ] && exit -1

mkdir -p $OUT_DIR_LIB/armeabi-v7a
mv "${LUAJIT_DIR}/src/${DYN_LIB_FILE_NAME}" "${OUT_DIR_LIB}/armeabi-v7a/${DYN_LIB_FILE_NAME}"
mv "${LUAJIT_DIR}/src/${STATIC_LIB_FILE_NAME}" "${OUT_DIR_LIB}/armeabi-v7a/${STATIC_LIB_FILE_NAME}"

# x86
echo -e "\n\nBuilding x86.."
make -C "${LUAJIT_DIR}" clean
NDKVER=$NDK/toolchains/x86-4.6
NDKP=$NDKVER/prebuilt/linux-x86/bin/i686-linux-android-
NDKF="--sysroot $NDK/platforms/android-$NDKABI/arch-x86"
make -C "${LUAJIT_DIR}" CROSS=$NDKP TARGET_FLAGS="$NDKF"
[ $? -ne 0 ] && exit -1

mkdir -p $OUT_DIR_LIB/x86
mv "${LUAJIT_DIR}/src/${DYN_LIB_FILE_NAME}" "${OUT_DIR_LIB}/x86/${DYN_LIB_FILE_NAME}"
mv "${LUAJIT_DIR}/src/${STATIC_LIB_FILE_NAME}" "${OUT_DIR_LIB}/x86/${STATIC_LIB_FILE_NAME}"

# copy includes
cp "${LUAJIT_DIR}/src/lauxlib.h" $OUT_INCLUDE_DIR
cp "${LUAJIT_DIR}/src/lua.h" $OUT_INCLUDE_DIR
cp "${LUAJIT_DIR}/src/luaconf.h" $OUT_INCLUDE_DIR
cp "${LUAJIT_DIR}/src/lualib.h" $OUT_INCLUDE_DIR
cp "${LUAJIT_DIR}/src/luajit.h" $OUT_INCLUDE_DIR

echo -e "\nCopying artifacts to ${JNI_SOURCES_DIR} directory.."

mkdir -p "${JNI_SOURCES_INC_DIR}"
cp -R "${OUT_INCLUDE_DIR}/." "${JNI_SOURCES_INC_DIR}/"

mkdir -p "${JNI_SOURCES_LIB_DIR}"
cp -R "${OUT_DIR_LIB}/." "${JNI_SOURCES_LIB_DIR}/"

echo -e "\nCopying luajava sources.."

javah -o "${JNI_SOURCES_DIR}/luajava.h" -classpath "${LUAJAVA_JAVA_DIR}" org.keplerproject.luajava.LuaState
cp -R "${LUAJAVA_JAVA_DIR}/." "${SPOT_JAVA_DIR}"
cp -R "${LUAJAVA_C_DIR}/." "${JNI_SOURCES_DIR}"

echo -e "Building luajava library..\n"
ndk-build -C "${JNI_SOURCES_DIR}" NDK_PROJECT_PATH=. APP_BUILD_SCRIPT=./Android.mk NDK_APPLICATION_MK=./Application.mk NDK_OUT=dist

echo -e "Copying luajava to ${SPLOT_JNI_LIBS_DIR}..\n"
mkdir -p "${SPLOT_JNI_LIBS_DIR}/armeabi"
mkdir -p "${SPLOT_JNI_LIBS_DIR}/armeabi-v7a"
mkdir -p "${SPLOT_JNI_LIBS_DIR}/x86"

cp ${JNI_SOURCES_DIR}/dist/local/armeabi/*.so "${SPLOT_JNI_LIBS_DIR}/armeabi"
cp ${JNI_SOURCES_DIR}/dist/local/armeabi-v7a/*.so "${SPLOT_JNI_LIBS_DIR}/armeabi-v7a"
cp ${JNI_SOURCES_DIR}/dist/local/x86/*.so "${SPLOT_JNI_LIBS_DIR}/x86"

echo -e "\nReady.\n"
