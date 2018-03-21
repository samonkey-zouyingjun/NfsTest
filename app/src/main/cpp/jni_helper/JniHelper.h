//
// Created by Administrator on 2018/3/19.
//

#ifndef NFS_JNIHELPER_H
#define NFS_JNIHELPER_H

#include <jni.h>
#include "JavaClassCache.h"

extern "C" {
JNIEXPORT void JNICALL
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_getService(
        JNIEnv *env, jobject instance, jlong pointer);
};

JNIEXPORT jint JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_read(
        JNIEnv *env, jobject instance, jlong handler,jint fd,jobject buffer,jint capacity);


#endif //NFS_JNIHELPER_H