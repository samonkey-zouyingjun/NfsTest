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

    //文件描述符,可以和指针转换
    private int mNativeFd;
    private long mOffset;

    public NfsFile(long nativeHandler, int nativeFd) {
        mNativeHandler = nativeHandler;
        mNativeFd = nativeFd;
    }

    @Override
    public int read(ByteBuffer buffer, int maxLen) throws IOException {
        try {
            final int bytesRead =
                    read(mNativeHandler, mNativeFd, buffer, Math.min(maxLen, buffer.capacity()));
            mOffset += bytesRead;
            return bytesRead;
        } catch(ErrnoException e) {
            throw new IOException("Failed to read file. Fd: " + mNativeFd, e);
        }
    }

    @Override
    public int write(ByteBuffer buffer, int length) throws IOException {
        try {
            final int bytesWritten = write(mNativeHandler, mNativeFd, buffer, length);
            mOffset += bytesWritten;
            return bytesWritten;
        } catch(ErrnoException e) {
            throw new IOException("Failed to write file. Fd: " + mNativeFd, e);
        }
    }

    @Override
    public long seek(long offset) throws IOException {
        if (mOffset == offset) {
            return mOffset;
        }

        try {
            mOffset = seek(mNativeHandler, mNativeFd, offset, 0);
            return mOffset;
        } catch (ErrnoException e) {
            throw new IOException("Failed to move to offset in file. Fd: " + mNativeFd, e);
        }
    }

    @Override
    public StructStat fstat() throws IOException {
        try {
            return fstat(mNativeHandler, mNativeFd);
        } catch (ErrnoException e) {
            throw new IOException("Failed to get stat of " + mNativeFd, e);
        }
    }

    @Override
    public void close() throws IOException {

    }

    private native int read(long handler, int fd, ByteBuffer buffer, int capacity)
            throws ErrnoException;

    private native int write(long handler, int fd, ByteBuffer buffer, int length)
            throws ErrnoException;

    private native long seek(long handler, int fd, long offset, int whence)
            throws ErrnoException;

    private native StructStat fstat(long handler, int fd) throws ErrnoException;

    private native void close(long handler, int fd) throws ErrnoException;
}
