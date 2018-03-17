#-------------------------------------------------------------
#设置cmake版本
cmake_minimum_required(VERSION 3.4.1)

#设置NDK编译变量
set(CMAKE_CXX_FLAGS
    "${CMAKE_CXX_FLAGS} -std=c++0x -O2 -D_FORTIFY_SOURCE=2 -fstack-protector-all -fPIE")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -pie")

#-------------------------------------------------------------
#是指头文件目录
include_directories(src/main/cpp)

#设置so库文件目录
set(libfolder "${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}")

#-------------------------------------------------------------
#添加编译库来自jnihelp
add_library( nfs_client
             SHARED
             src/main/cpp/jni_helper/JniHelper.c
             src/main/cpp/jni_helper/JavaClassCache.c
             src/main/cpp/nfs_client/NfsClient.c )
#log lib
find_library( log-lib
              log )

#添加目标库引用自libnfs.so
add_library(libnfsclient
          SHARED
          IMPORTED)

#设置libnfsclient引用位置
set_target_properties(libnfsclient
                    PROPERTIES IMPORTED_LOCATION
                    ${libfolder}/libnfs.so)

#-------------------------------------------------------------
#把各编译好的库连接到一起
target_link_libraries( nfs_client
                       ${log-lib}
                       libnfsclient )