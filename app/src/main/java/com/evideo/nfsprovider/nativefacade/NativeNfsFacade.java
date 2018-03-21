package com.evideo.nfsprovider.nativefacade;

/**
 * Created by zouyingjun on 2018/3/19.
 * java 层libnfs 代理
 */

public class NativeNfsFacade implements NfsClient {
    @Override
    public void findService() {
        getService();
    }

    /**
     * 获取nfs本地服务
     */
    private native void getService();
}
