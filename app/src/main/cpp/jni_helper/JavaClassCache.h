//
// Created by Administrator on 2018/3/19.
//

#ifndef NFS_JAVACLASSCACHE_H
#define NFS_JAVACLASSCACHE_H

#include <unordered_map>
#include <jni.h>

namespace NfsClient {
    class JavaClassCache {
    public:
        jclass get(JNIEnv *env, const char *name);
    private:
        std::unordered_map<std::string, jclass> cache_;
    };
}

#endif //NFS_JAVACLASSCACHE_H
