//
// Created by zouyingjun on 2018/3/19.
//

#ifndef NFS_JNIHELPER_H
#define NFS_JNIHELPER_H

#include <jni.h>
#include "JavaClassCache.h"
#include "logger/logger.h"
#include "nfsc/libnfs.h"
#include "nfsc/libnfs-raw.h"
#include "nfsc/libnfs-raw-mount.h"

extern "C" {


JNIEXPORT jobject JNICALL
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_getService(
        JNIEnv *env, jobject instance, jlong pointer);

JNIEXPORT jint JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_read(
        JNIEnv *env, jobject instance, jlong handler, jint fd, jobject buffer, jint capacity);

JNIEXPORT jint JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_write(
        JNIEnv *env, jobject instance, jlong handler, jint fd, jobject buffer, jint length);

JNIEXPORT jlong JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_seek(
        JNIEnv *env, jobject instance, jlong handler, jint fd, jlong offset, jint whence);

JNIEXPORT jobject JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_fstat(
        JNIEnv *env, jobject instance, jlong handler, jint fd);

JNIEXPORT void JNICALL
Java_com_evideo_nfsprovider_nativefacade_NfsFile_close(
        JNIEnv *env, jobject instance, jlong handler, jint fd);

JNIEXPORT jint JNICALL
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_openFile(
        JNIEnv *env, jobject instance, jlong handler, jstring uri_, jstring mode_);

JNIEXPORT jobject JNICALL
Java_com_evideo_nfsprovider_MainActivity_openDirSync2(JNIEnv *env, jobject instance,
                                                      jstring path_);

#endif //NFS_JNIHELPER_H
}