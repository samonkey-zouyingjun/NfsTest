//
// Created by Administrator on 2018/3/19.
//

#include "JniHelper.h"
#include "nfs_client/NfsClient.h"

namespace {
    NfsClient::JavaClassCache classCache_;//获取java类
}

void
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_getService(
        JNIEnv *env, jobject instance, jlong pointer) {
    NfsClient::NfsClient *client = new NfsClient::NfsClient();
    client->getService();
};

jint
Java_com_evideo_nfsprovider_nativefacade_NfsFile_read(
        JNIEnv *env,
        jobject instance,
        jlong pointer,
        jint fd,
        jobject buffer_,
        jint capacity) {

    void *buffer = env->GetDirectBufferAddress(buffer_);

    NfsClient::NfsClient *client = reinterpret_cast<NfsClient::NfsClient*>(pointer);


};