LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := libluajit-prebuilt
LOCAL_SRC_FILES := $(LOCAL_PATH)/../luajit-dist/lib/$(TARGET_ARCH_ABI)/libluajit.a
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/../luajit-dist/include
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_MODULE     := luajava-1.1
LOCAL_SRC_FILES  := luajava.c
#LOCAL_SHARED_LIBRARIES += libluajit-prebuilt
LOCAL_WHOLE_STATIC_LIBRARIES += libluajit-prebuilt
include $(BUILD_SHARED_LIBRARY)
