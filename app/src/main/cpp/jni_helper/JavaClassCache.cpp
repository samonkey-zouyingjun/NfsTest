//
// Created by Administrator on 2018/3/19.
//创建 obj 参数所引用对象的新全局引用。全局引用通过调用DeleteGlobalRef() 来显式撤消。


#include "JavaClassCache.h"
namespace NfsClient {
    jclass JavaClassCache::get(JNIEnv *env, const char *name_) {
        std::string name(name_);
        jclass &value = cache_[name];
        if (value == NULL) {
            jclass localRef = env->FindClass(name_);
            if (localRef == NULL) {
                return NULL;
            }
            value = reinterpret_cast<jclass>(env->NewGlobalRef(localRef));
        }
        return value;
    }
}

