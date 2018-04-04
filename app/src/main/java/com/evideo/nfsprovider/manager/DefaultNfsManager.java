package com.evideo.nfsprovider.manager;

import android.content.Context;
import android.util.Log;

import com.evideo.nfsprovider.util.NfsFileUtils;

import java.io.File;

/**
 * Created by zouyingjun on 2018/3/21.
 */

public class DefaultNfsManager extends NfsManager {
    private static final String TAG = "DefaultNfsManager";

    public DefaultNfsManager(Context paramContext) {
        super(paramContext);
    }

    private boolean makeDirs(String path) {
        File dir = new File(path);
        return dir.exists() || dir.mkdirs();
    }

    private void sleep(int paramInt) {
        long l = paramInt;
        try {
            Thread.sleep(l);
            return;
        } catch (InterruptedException localInterruptedException) {
            localInterruptedException.printStackTrace();
        }
    }

    public String getNfsRoot() {
        return "/sdcard/nfs";
    }

    /**
     * 通过mount 挂载nfs
     * 非主线程调用
     * @param ip 设备ip
     * @param sharePath 设备共享文件夹
     * @param mountPath 挂载节点
     * @return
     */
    public boolean mountNfs(String ip, String sharePath, String mountPath) {
        boolean mountSucceed = false;
        mountPath = getNfsRoot() + "/" + mountPath;
        if ((!makeDirs("/data/etc")) || (!makeDirs(getNfsRoot())) || (!makeDirs(mountPath))) {
            Log.e(TAG, "mountNfs make dirs failed ");
            return false;
        }
        Log.d(TAG, "execute ok1!");
        NfsFileUtils.execute("busybox mount -t nfs -o nolock \"" +
                ip + ":" + sharePath + "\" " + mountPath);
        Log.d(TAG, "execute ok2!");
        int i = 0;
//        192.168.199.236:/volume1/share /mnt/shell/emulated/0/nfs nfs
        String prefix = ip+":"+sharePath+" "+mountPath;
        Log.d(TAG, "mountNfs: pre:"+prefix);
        for (; ; ) {
            Log.d(TAG, "i:"+i);
            sleep(50);

            mountSucceed = isNfsMounted(prefix);
            if ((mountSucceed) || (i >= 10)) {
                if (mountSucceed) {
                    break;
                }
                new File(mountPath).delete();
                return false;
            }
            i += 1;
        }
        Log.d(TAG, "mount finish!");
        return mountSucceed;
    }

    /**
     * unmount 操作 卸载nfs
     * @param shareFile /data/nfs/192.168.199.127/k20s/4k.mp4
     * @return
     */
    public boolean umountNfs(File shareFile) {
        String str = shareFile.getPath();
        if (!new File(str.replace("\"", "")).exists()) {
            return true;
        }
        NfsFileUtils.execute("busybox umount -fl " + str);
        int i = 0;
        for (; ; ) {
            sleep(50);
            boolean bool = NfsManager.isNfsMounted(shareFile.getPath(), true);
            if ((!bool) || (i >= 10)) {
                if (!bool) {
                    new File(str).delete();
                }
                if (!bool) {
                    break;
                }
                return false;
            }
            i += 1;
        }
        return false;
    }
}
