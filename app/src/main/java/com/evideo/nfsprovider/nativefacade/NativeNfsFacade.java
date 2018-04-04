package com.evideo.nfsprovider.nativefacade;

import android.system.ErrnoException;
import android.system.StructStat;

import java.io.IOException;
import java.util.List;

/**
 * Created by zouyingjun on 2018/3/19.
 * java 层libnfs 代理
 */

public class NativeNfsFacade implements NfsClient {

    static {
        System.loadLibrary("nfs_client");
    }

    @Override
    public List<String> findService() {
        return getService();
    }

    @Override
    public void reset() {

    }

    @Override
    public SmbDir openDir(String uri) throws IOException {
        return null;
    }

    @Override
    public StructStat stat(String uri) throws IOException {
        return null;
    }

    @Override
    public void createFile(String uri) throws IOException {

    }

    @Override
    public void mkdir(String uri) throws IOException {

    }

    @Override
    public void rename(String uri, String newUri) throws IOException {

    }

    @Override
    public void unlink(String uri) throws IOException {

    }

    @Override
    public void rmdir(String uri) throws IOException {

    }

    @Override
    public SmbFile openFile(String uri, String mode) throws IOException {
        return null;
    }


    /**
     * 获取nfs本地服务
     */
    private native List<String> getService();

//    private native long nativeInit(boolean debug, long cacheHandler);
//
//    private native void nativeDestroy(long handler);

//    private native int openDir(long handler, String uri) throws ErrnoException;

    private native int openFile(long handler, String uri, String mode) throws ErrnoException;

//    private native StructStat stat(long handler, String uri) throws ErrnoException;
//
//    private native void createFile(long handler, String uri) throws ErrnoException;
//
//    private native void mkdir(long handler, String uri) throws ErrnoException;
//
//    private native void rmdir(long handler, String uri) throws ErrnoException;
//
//    private native void rename(long handler, String uri, String newUri) throws ErrnoException;
//
//    private native void unlink(long handler, String uri) throws ErrnoException;

}
