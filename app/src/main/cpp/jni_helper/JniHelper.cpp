
#include <nfs_client/NfsClient.h>
#include "JniHelper.h"

#include <stdlib.h>
#include <asm/errno.h>
#include <errno.h>
#include <sys/statvfs.h>
#include <stdio.h>

#include <fcntl.h>
#include <inttypes.h>
#include <string.h>


#define TAG "NativeNfsClient"
#define NFS_MAX_FD  255

//char buf[3*1024*1024+337];

namespace {
    NfsClient::JavaClassCache classCache_;//获取java类
}


/**
 * 同步打开文件夹
 * @param env
 * @param instance
 * @param path_
 */
void
Java_com_evideo_nfsprovider_MainActivity_openDirSync(JNIEnv *env, jobject instance, jstring path_) {
    const char *url = env->GetStringUTFChars(path_, 0);

    struct nfs_context *nfs = NULL;
    int i, ret, res;
    uint64_t offset;
//    struct client client;
//    struct nfs_stat_64 st;
    struct nfsfh  *nfsfh;
    struct nfsdir *nfsdir;
//    struct nfsdirent *nfsdirent;
    struct statvfs svfs;
//    exports export, tmp;
    char *server = NULL, *path = NULL, *strp;

    if (url == NULL) {
        LOGD(TAG, "No URL specified.\n");

        return;
    }

    if (strncmp(url, "nfs://", 6)) {
        LOGD(TAG, "Invalid URL specified.\n");
        return;
    }

    server = strdup(url + 6);
    if (server == NULL) {
        LOGD(TAG, "Failed to strdup server string\n");
        return;
    }
    if (server[0] == '/' || server[0] == '\0') {
        LOGD(TAG, "Invalid server string.\n");
        free(server);
        return;
    }
    strp = strchr(server, '/');
    if (strp == NULL) {
        LOGD(TAG, "Invalid URL specified.\n");
        free(server);
        return;
    }
    path = strdup(strp);
    if (path == NULL) {
        LOGD(TAG, "Failed to strdup server string\n");
        free(server);
        return;
    }
    if (path[0] != '/') {
        LOGD(TAG, "Invalid path.\n");
        free(server);
        free(path);
        return;
    }

    *strp = 0;

//    client.server = server;
//    client.export = path;
//    client.is_finished = 0;





    // TODO

    env->ReleaseStringUTFChars(path_, url);
}


/**
 * 通过广播获取服务列表
 * @param env
 * @param instance
 * @param pointer
 * @return
 */
jobject
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_getService(JNIEnv *env,
                                                                    jobject instance,
                                                                    jlong pointer) {
    LOGD(TAG, "getServoce start!");

    struct nfs_server_list *srvrs;
    struct nfs_server_list *srv;

    //取得arrylist类
    jclass list_cls = classCache_.get(env, "java/util/ArrayList");
    //获得得构造函数Id
    jmethodID list_costruct = env->GetMethodID(list_cls, "<init>", "()V");
    //创建一个Arraylist集合对象
    jobject list_obj = env->NewObject(list_cls, list_costruct);
    //或得Arraylist类中的 add()方法ID，其方法原型为： boolean add(Object object) ;
    jmethodID list_add = env->GetMethodID(list_cls, "add", "(Ljava/lang/Object;)Z");

    srvrs = nfs_find_local_servers();
    for (srv = srvrs; srv; srv = srv->next) {
        LOGD(TAG, "NFS SERVER @ %s\n", srv->addr);
        jstring str = env->NewStringUTF(srv->addr);
        //执行Arraylist类实例的add方法，添加一个String对象
        env->CallBooleanMethod(list_obj, list_add, str);
    }

    free_nfs_srvr_list(srvrs);

    LOGD(TAG, "getServoce end!");
    return list_obj;
};





/**
 * 同步方法
 * 打开文件夹
 * @param env
 * @param instance
 * @param path_
 */
jobject
Java_com_evideo_nfsprovider_MainActivity_openDirSync2(JNIEnv *env, jobject instance,
                                                      jstring path_) {
    const char *url = env->GetStringUTFChars(path_, 0);
    LOGD(TAG, "open url : %s\n",url);
    char *server = NULL, *path = NULL, *strp;

    //取得arrylist类
    jclass list_cls = classCache_.get(env, "java/util/ArrayList");
    //获得得构造函数Id
    jmethodID list_costruct = env->GetMethodID(list_cls, "<init>", "()V");
    //创建一个Arraylist集合对象
    jobject list_obj = env->NewObject(list_cls, list_costruct);
    //或得Arraylist类中的 add()方法ID，其方法原型为： boolean add(Object object) ;
    jmethodID list_add = env->GetMethodID(list_cls, "add", "(Ljava/lang/Object;)Z");



    //url 解析
    if (url == NULL) {
        LOGD(TAG, "No URL specified.\n");
        return NULL;
    }

    if (strncmp(url, "nfs://", 6)) {
        LOGD(TAG, "Invalid URL specified.\n");
        return NULL;
    }

    server = strdup(url + 6);
    if (server == NULL) {
        LOGD(TAG, "Failed to strdup server string\n");
        return NULL;
    }
    if (server[0] == '/' || server[0] == '\0') {
        LOGD(TAG, "Invalid server string.\n");
        free(server);
        return NULL;
    }
    strp = strchr(server, '/');
    if (strp == NULL) {
        LOGD(TAG, "Invalid URL specified.\n");
        free(server);
        return NULL;
    }
    path = strdup(strp);
    if (path == NULL) {
        LOGD(TAG, "Failed to strdup server string\n");
        free(server);
        return NULL;
    }
    if (path[0] != '/') {
        LOGD(TAG, "Invalid path.\n");
        free(server);
        free(path);
        return NULL;

    }


    *strp = 0;

    LOGD(TAG, "no err for parse url:%s,path:%s.\n",server,path);
    struct nfs_context *nfs = NULL;

    //初始化
    nfs = nfs_init_context();
    if (nfs == NULL) {
        LOGE(TAG,"failed to init context\n");
        return NULL;
    }
    int ret;

    //挂载
    ret = nfs_mount(nfs, server, path);

    if (ret != 0) {
        LOGE(TAG,"Failed to opendir(\"/\") %s\n", nfs_get_error(nfs));
        return NULL;
    }

    LOGD(TAG, "mount success!.\n");


    struct nfsdirent *nfsdirent;
    struct nfsdir *nfsdir;
    struct nfs_stat_64 st;

    //打开文件夹

    ret = nfs_opendir(nfs, "/", &nfsdir);
    if (ret != 0) {
        LOGD(TAG,"Failed to opendir(\"/\") %s\n", nfs_get_error(nfs));
        return NULL;
    }

    LOGD(TAG,"open dir ok");

    while((nfsdirent = nfs_readdir(nfs, nfsdir)) != NULL) {
        LOGD(TAG,"open %s",nfsdirent->name);
        char path[1024];

        if (!strcmp(nfsdirent->name, ".") || !strcmp(nfsdirent->name, "..")) {
            continue;
        }

        sprintf(path, "%s/%s", "/", nfsdirent->name);
        ret = nfs_stat64(nfs, path, &st);
        if (ret != 0) {
            LOGD(TAG, "Failed to stat(%s) %s\n", path, nfs_get_error(nfs));
            continue;
        }

        jstring str = env->NewStringUTF(nfsdirent->name);
        //执行Arraylist类实例的add方法，添加一个String对象
        env->CallBooleanMethod(list_obj, list_add, str);

    }


    LOGD(TAG,"read finish!");

    //关闭文件夹
    nfs_closedir(nfs, nfsdir);


    //销毁连接和NfsContext
    free(server);
    free(path);
    if (nfs != NULL) {
        nfs_destroy_context(nfs);
    }


    env->ReleaseStringUTFChars(path_, url);

    return list_obj;
}


jint
Java_com_evideo_nfsprovider_nativefacade_NativeNfsFacade_openFile(
        JNIEnv *env, jobject instance, jlong pointer, jstring uri_, jstring mode_) {
    int fd = -1;

    const char *uri = env->GetStringUTFChars(uri_, 0);
    if (uri == NULL) {
        return fd;
    }
    const char *mode = env->GetStringUTFChars(mode_, 0);
    if (mode == NULL) {
        env->ReleaseStringUTFChars(uri_, uri);
        return fd;
    }

    int flag = -1;
    if (mode[0] == 'r') {
        if (mode[1] == '\0') {
            flag = O_RDONLY;
        } else if (mode[1] == 'w') {
            flag = O_RDWR;
            if (mode[2] == 't' && mode[3] == '\0') {
                flag |= O_TRUNC;
            }
        }
    } else if (mode[0] == 'w') {
        flag = O_WRONLY;
        if (mode[1] == 'a') {
            flag |= O_APPEND;
        } else if (mode[1] == '\0') {
            flag |= O_TRUNC;
        }
    }


    if (flag >= 0) {
        NfsClient::NfsClient *client =
                reinterpret_cast<NfsClient::NfsClient*>(pointer);
        fd = client->OpenFile(uri, flag, 0);
    }

    if (fd < 0) {
        int err = -fd;
        switch (err) {
            case ENODEV:
            case ENOENT:
                LOGE(TAG,  "File at %s can't be found.", uri);
            case EACCES:
                LOGW(TAG, "No access to file at %s.", uri);
                break;
            default:
                LOGE(TAG, "openFile.", err);
                LOGE(TAG, "openFile.", err);
        }
    }

    env->ReleaseStringUTFChars(uri_, uri);
    env->ReleaseStringUTFChars(mode_, mode);

    return fd;
}