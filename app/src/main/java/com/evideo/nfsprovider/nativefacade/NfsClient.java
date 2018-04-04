package com.evideo.nfsprovider.nativefacade;

import android.system.StructStat;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2018/3/19.
 */

public interface NfsClient {
    //发现本地nfs服务器
    List<String> findService();

    void reset();

    SmbDir openDir(String uri) throws IOException;

    StructStat stat(String uri) throws IOException;

    void createFile(String uri) throws IOException;

    void mkdir(String uri) throws IOException;

    void rename(String uri, String newUri) throws IOException;

    void unlink(String uri) throws IOException;

    void rmdir(String uri) throws IOException;

    SmbFile openFile(String uri, String mode) throws IOException;
}
