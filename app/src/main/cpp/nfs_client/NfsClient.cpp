//
// Created by zouyingjun on 2018/3/19.
// c++ 层libnfs 代理，api 再次封装 LOGD(TAG, "Destroying SambaClient.");打印必要日志

#include "NfsClient.h"
#include "logger/logger.h"
#include <errno.h>
#define TAG "NativeNfsClient"

namespace NfsClient{
    /**
     * 获取服务列表
     */
    void NfsClient::getService() {
        LOGD(TAG, "getServoce start!");

        struct nfs_server_list *srvrs;
        struct nfs_server_list *srv;

        srvrs = nfs_find_local_servers();
        for (srv=srvrs; srv; srv = srv->next) {
            LOGD(TAG, "NFS SERVER @ %s\n", srv->addr);
        }

        free_nfs_srvr_list(srvrs);

        LOGD(TAG, "getServoce end!");
    }

//    /**
//     * 读取文件
//     * @param fd
//     * @param buffer
//     * @param maxlen
//     * @return
//     */
//    ssize_t
//    NfsClient::ReadFile(const int fd, void *buffer, const size_t maxlen){
//        LOGV(TAG, "Reading max %lu bytes from file with fd %x", maxlen, fd);
//
//        if (nfs_fd_list[fd].is_nfs == 1) {
//            int ret;
//
//            LD_NFS_DPRINTF(9, "read(fd:%d count:%d)", fd, (int)maxlen);
//            if ((ret = nfs_read(nfs_fd_list[fd].nfs, nfs_fd_list[fd].fh,
//                                maxlen, buffer)) < 0) {
//                errno = -ret;
//                return -1;
//            }
//            return ret;
//        }
//
//        const ssize_t size = real_read(fd, buffer, maxlen);
//        if (size < 0) {
//            int err = errno;
//            LOGE(TAG, "Failed to read file with fd %x. Errno: %x", fd, err);
//            return -err;
//        } else {
//            LOGV(TAG, "Read %ld bytes.", size);
//        }
//        return 0;
//    }
}
