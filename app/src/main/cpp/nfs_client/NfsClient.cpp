//
// Created by zouyingjun on 2018/3/19.
// c++ 层libnfs 代理，api 再次封装 LOGD(TAG, "Destroying SambaClient.");打印必要日志

#include "NfsClient.h"
#include "logger/logger.h"
#include <errno.h>
#include<string.h>
#include<fcntl.h>
#include <stdlib.h>
#include <jni.h>

#define TAG "NativeNfsClient"
#define NFS_MAX_FD  255

namespace NfsClient{

    struct nfs_fd_list {
        int is_nfs;
        struct nfs_context *nfs;
        struct nfsfh *fh;

        /* so we can reopen and emulate dup2() */
        const char *path;
        int flags;
        mode_t mode;
    };

    static struct nfs_fd_list nfs_fd_list[NFS_MAX_FD];

    /**
     * 打开文件或文件夹
     * @param path
     * @param flag
     * @param mode
     * @return fd文件指针
     */
    int NfsClient::OpenFile(const char *path, const int flag, const mode_t mode) {
        LOGD(TAG, "Opening file at %s with flag %x.", path, flag);

        if (!strncmp(path, "nfs:", 4)) {
            struct nfs_context *nfs;
            struct nfs_url *url;
            struct nfsfh *fh = NULL;
            int ret, fd;

            LOGD(TAG, "open(%s, %x, %o)", path, flag, mode);
            nfs = nfs_init_context();
            if (nfs == NULL) {
                LOGV(TAG, "Failed to create context");
                errno = ENOMEM;
                return -1;
            }

            url = nfs_parse_url_full(nfs, path);
            if (url == NULL) {
                LOGV(TAG, "Failed to parse URL: %s\n",
                               nfs_get_error(nfs));
                nfs_destroy_context(nfs);
                errno = EINVAL;
                return -1;
            }

            if (nfs_mount(nfs, url->server, url->path) != 0) {
                LOGV(TAG, "Failed to mount nfs share : %s\n",
                               nfs_get_error(nfs));
                nfs_destroy_url(url);
                nfs_destroy_context(nfs);
                errno = EINVAL;
                return -1;
            }

            if (flag & O_CREAT) {
                if ((ret = nfs_creat(nfs, url->file, mode, &fh)) != 0) {
                    LOGV(TAG, "Failed to creat nfs file : "
                            "%s\n", nfs_get_error(nfs));
                    nfs_destroy_url(url);
                    nfs_destroy_context(nfs);
                    errno = -ret;
                    return -1;
                }
            } else {
                if ((ret = nfs_open(nfs, url->file, flag, &fh)) != 0) {
                    LOGV(TAG, "Failed to open nfs file : "
                            "%s\n", nfs_get_error(nfs));
                    nfs_destroy_url(url);
                    nfs_destroy_context(nfs);
                    errno = -ret;
                    return -1;
                }
            }

            fd = nfs_get_fd(nfs);
            if (fd >= NFS_MAX_FD) {
                LOGV(TAG, "Too many files open");
                nfs_destroy_url(url);
                nfs_destroy_context(nfs);
                errno = ENFILE;
                return -1;
            }


            nfs_fd_list[fd].is_nfs     = 1;
            nfs_fd_list[fd].nfs        = nfs;
            nfs_fd_list[fd].fh         = fh;
            nfs_fd_list[fd].path       = strdup(path);
            nfs_fd_list[fd].flags      = flag;
            nfs_fd_list[fd].mode       = mode;

            nfs_destroy_url(url);

            LOGV(TAG,"open(%s) == %d", path, fd);
            return fd;
        }

        LOGE(TAG,"openfile path must start with nfs:");

        return -1;
    }

}
