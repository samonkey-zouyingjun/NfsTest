package com.evideo.nfsprovider.manager;

import android.content.Context;

import java.io.File;

/**
 * Created by Administrator on 2018/3/21.
 */

public class DefaultNfsManager extends NfsManager {

    public DefaultNfsManager(Context context) {
        super(context);
    }

    @Override
    public String getNfsRoot() {
        return "/mnt/nfs";
    }

    @Override
    public boolean mountNfs(String str, String str2, String str3) {
        return false;
    }

    @Override
    public boolean umountNfs(File file) {
        return false;
    }
}
