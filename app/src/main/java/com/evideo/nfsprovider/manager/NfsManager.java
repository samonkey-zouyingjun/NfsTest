package com.evideo.nfsprovider.manager;

import android.content.Context;
import android.util.Log;

import com.evideo.nfsprovider.model.NfsDevice;
import com.evideo.nfsprovider.model.NfsFolder;
import com.evideo.nfsprovider.scan.CmdNfsScan;
import com.evideo.nfsprovider.scan.IPNfsConnect;
import com.evideo.nfsprovider.scan.NfsScan;
import com.evideo.nfsprovider.scan.OnNfsSearchListener;
import com.evideo.nfsprovider.scan.PortNfsScan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * nfs管理基类，封装了一些基础方法
 */
public abstract class NfsManager implements NfsMount {
    static File sRootFile;
    protected ArrayList<NfsDevice> devices = new ArrayList();
    Context mContext;
    NfsScan mNfsScan = null;
    protected OnNfsSearchListener onNfsSearchListener;

    NfsManager(Context context) {
        this.mContext = context;
    }

    /**
     * 根据端口查找
     * @param port
     */
    public final void scanDevices(int port) {
        this.mNfsScan = new PortNfsScan(this.mContext, port);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    /**
     * 直接查找
     */
    public final void scanDevices() {
        this.mNfsScan = new CmdNfsScan(this.mContext);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    /**
     * 根据ip搜索
     * @param ip
     */
    public final void scanDevices(String ip) {
        this.mNfsScan = new IPNfsConnect(ip);
        this.mNfsScan.setOnNfsSearchListener(this.onNfsSearchListener);
        this.mNfsScan.start();
    }

    public final boolean isSearching() {
        return this.mNfsScan != null && this.mNfsScan.isScanning();
    }

    public void setOnNfsSearchListener(OnNfsSearchListener onNfsSearchListener) {
        this.onNfsSearchListener = onNfsSearchListener;
    }

    /**
     * 根据设备获取共享路径信息
     * @param nfsDevice
     * @return
     */
    public ArrayList<NfsFolder> openDevice(NfsDevice nfsDevice) {
        ArrayList<NfsFolder> nfsFolders = new ArrayList();
        String line = "";
        try {
            NfsFolder folder;
            Process proc = Runtime.getRuntime().exec(new String[]{"/system/bin/nfsprobe", "-e", nfsDevice.ip});
            BufferedReader buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            proc.waitFor();
            while (true) {
                try {
                    line = buf.readLine();
                    if (line == null) {
                        break;
                    }
                    int i = line.length() - 1;
                    while (i > 0 && line.charAt(i) != ' ') {
                        i--;
                    }
                    line = line.substring(0, i).trim();
                    folder = new NfsFolder();
                    folder.setFolderPath(line);
                    folder.ip = nfsDevice.ip;
                    nfsFolders.add(folder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return nfsFolders;
    }


    /**
     * /proc/mounts
     * 192.168.199.236:/volume1/share /mnt/shell/emulated/0/a nfs ...
     * @param ip        192.168.199.236
     * @param sharePath /volume1/share
     * @param mountPath /mnt/shell/emulated/0/a
     * @return
     */
    public boolean isNfsMounted(String ip, String sharePath, String mountPath) {
        return isNfsMounted(mountPath,true);
    }

    /**
     * 查找/proc/mounts是否包含以prefix开头的信息
     * @param prefix
     * @return
     */
    public static boolean isNfsMounted(String prefix) {
        IOException e;
        Throwable th;
        File file = new File("/proc/mounts");
        if (file.canRead()) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                String line = null;
                do {
                    try {
                        line = reader2.readLine();
                        if (line == null) {
                            if (reader2 != null) {
                                try {
                                    reader2.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                            return false;
                        }
                    } catch (IOException e3) {
                        reader = reader2;
                    } catch (Throwable th2) {
                        reader = reader2;
                    }
                } while (!line.startsWith(prefix));
                if (reader2 == null) {
                    return true;
                }
                try {
                    reader2.close();
                    return true;
                } catch (IOException e22) {
                    e22.printStackTrace();
                    return true;
                }
            } catch (IOException e4) {
                try {
                    e4.printStackTrace();
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                        }
                    }
                    try {
                        throw th;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
        return false;
    }


    /**
     * 查找mounts 是否包含以mountPath为更目录的信息
     * @param mountPath
     * @param beTrue 必须传入true
     * @return
     */
    public static boolean isNfsMounted(String mountPath, boolean beTrue) {
        File file = new File("/proc/mounts");
        boolean find = false;
        Throwable th;

        if (file.canRead()) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                String line = null;
                do {
                    try {
                        line = reader2.readLine();
                        if (line == null) {
                            if (reader2 != null) {
                                try {
                                    reader2.close();
                                } catch (IOException e2) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    } catch (IOException e3) {
                        reader = reader2;
                    } catch (Throwable th2) {
                        reader = reader2;
                    }
                } while (isStartWithMountPath(line,mountPath,beTrue));
                if (reader2 == null) {
                    return true;
                }
                try {
                    reader2.close();
                    return true;
                } catch (IOException e22) {
                    e22.printStackTrace();
                    return true;
                }
            } catch (IOException e4) {
                try {
                    e4.printStackTrace();
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th3) {
                    th = th3;
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e2222) {
                            e2222.printStackTrace();
                        }
                    }
                    try {
                        throw th;
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        }
        return find;
    }

    private static final String TAG = "NfsManager";
    /**
     *
     * 是否包含mountPath的记录
     * @param line
     * @param mountPath
     * @param b
     * @return
     */
    private static boolean isStartWithMountPath(String line, String mountPath , boolean b){

        String[] split = line.split(" ");

        if((split.length > 2) && (split[2].equals("nfs")) && (b)
//            &&mountPath.startsWith(split[1])){
                && (split[1].equals(mountPath))
                && (split[0].equals(mountPath))) {

            Log.d(TAG, "isStartWithMountPath: true");
            return true;
        }

        Log.d(TAG, "isStartWithMountPath: false");
        return false;
    }
}
