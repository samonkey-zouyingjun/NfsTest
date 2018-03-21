package com.evideo.nfsprovider.nativefacade;

import android.system.ErrnoException;
import android.system.StructStat;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by zouyingjun on 2018/3/20.
 */

public class NfsFile implements SmbFile {

    private final long mNativeHandler;

    private int mNativeFd;
    private long mOffset;

    public NfsFile(long nativeHandler, int nativeFd) {
        mNativeHandler = nativeHandler;
        mNativeFd = nativeFd;
    }

    @Override
    public int read(ByteBuffer buffer, int maxLen) throws IOException {
        return 0;
    }

    @Override
    public int write(ByteBuffer buffer, int length) throws IOException {
        return 0;
    }

    @Override
    public long seek(long offset) throws IOException {
        return 0;
    }

    @Override
    public StructStat fstat() throws IOException {
        return null;
    }

    @Override
    public void close() throws IOException {

    }

    private native int read(long handler, int fd, ByteBuffer buffer, int capacity)
            throws ErrnoException;
}
