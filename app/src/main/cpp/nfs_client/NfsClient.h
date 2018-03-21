//
// Created by Administrator on 2018/3/19.
//

#ifndef NFS_NFSCLIENT_H
#define NFS_NFSCLIENT_H

#include "nfs_include/libnfs.h"

namespace NfsClient {
    class NfsClient {
    public:
        /**
         * nfs_find_local_servers
         */
        void getService();

        ssize_t ReadFile(const int fd, void *buffer, const size_t maxlen);
    };
}


#endif //NFS_NFSCLIENT_H
