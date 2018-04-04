//
// Created by Administrator on 2018/3/19.
//

#ifndef NFS_NFSCLIENT_H
#define NFS_NFSCLIENT_H

#include <sys/types.h>
#include "nfsc/libnfs.h"
#include<fcntl.h>

namespace NfsClient {
    class NfsClient {
    public:
        int OpenFile(const char *url, const int flag, const mode_t mode);
    };
}


#endif //NFS_NFSCLIENT_H
