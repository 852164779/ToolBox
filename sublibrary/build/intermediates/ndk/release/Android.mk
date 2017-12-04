LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := signUtil
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := \
	-llog \

LOCAL_SRC_FILES := \
	D:\AndroidStudioProjects\ToolBox\sublibrary\src\main\jni\sign.cpp \

LOCAL_C_INCLUDES += D:\AndroidStudioProjects\ToolBox\sublibrary\src\main\jni
LOCAL_C_INCLUDES += D:\AndroidStudioProjects\ToolBox\sublibrary\src\release\jni

include $(BUILD_SHARED_LIBRARY)
