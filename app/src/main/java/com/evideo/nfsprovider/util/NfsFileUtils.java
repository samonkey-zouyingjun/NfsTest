package com.evideo.nfsprovider.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.evideo.nfsprovider.file.FileType;
import com.evideo.nfsprovider.model.NfsFolder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by Administrator on 2018/3/21.
 */


public class NfsFileUtils {

    public static NfsFolder sNfsFolder;


//    /**
//     * 触发底层脚本进行NAS挂载
//     * @param mount true挂载/false卸载
//     */
//    public void triggerMountShell(boolean mount){
//        SystemProperties.set("ctl.start", mount ? "nfs_mount" : "nfs_unmount");
//        EvLog.d(TAG,String.format("wkw mountNas(%s)",mount));
//    }


    public static void setNfsFolder(String mountPoint){
        if(sNfsFolder != null){
            sNfsFolder.setMountedPoint(mountPoint);
        }
    }

   /* private static final String SHELL_HEAD = "#!/system/bin/sh";
    private static final String SHELL_LOG_PATH = "/data/etc/nfs_log";
    private static final String SHELL_PATH = "/data/etc/nfsmanager.sh";
    private static final String SHELL_ROOT = "/data/etc";*/

    private static final String SHELL_HEAD = "#!/system/bin/sh";
    private static final String SHELL_LOG_PATH = "/sdcard/etc/nfs_log";
    private static final String SHELL_PATH = "/sdcard/etc/nfsmanager.sh";
    private static final String SHELL_ROOT = "/sdcard/etc";

    /**
     * 执行shell命令
     * @param cmd
     */
    public static void execute(String cmd) {
        File shellDir = new File(SHELL_ROOT);
        if (shellDir.exists() || shellDir.mkdirs()) {
            File shellLog = new File(SHELL_LOG_PATH);
            try {
                if (shellLog.exists()) {
                    shellLog.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                shellLog.createNewFile();
                shellLog.setReadable(true);
                shellLog.setExecutable(true);
                shellLog.setWritable(true);
                String command = cmd + " > " + SHELL_LOG_PATH + " 2>&1";
                try {
                    if (ShellFileWrite(new String[]{SHELL_HEAD, command})) {
                        try {
                            System.gc();
                            Thread.sleep(20);
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                        //执行sh脚本,必须以系统服务的方式执行，需要顶层配合

//                        cmdSh(SHELL_PATH);

//                        /**
//                         * 触发底层脚本进行NAS挂载
//                         * @param mount true挂载/false卸载
//                         */
//                        public void triggerMountShell(boolean mount){
//                            SystemProperties.set("ctl.start", mount ? "nfs_mount" : "nfs_unmount");
//                            EvLog.d(TAG,String.format("wkw mountNas(%s)",mount));
//                        }


                        try {
                            Class.forName("android.os.SystemProperties")
                                    .getDeclaredMethod("set",
                                            new Class[]{String.class, String.class}).
                                    invoke(null, new Object[]{"ctl.start", "nfs_mount"});
                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e3) {
                            e3.printStackTrace();
                        } catch (IllegalArgumentException e4) {
                            e4.printStackTrace();
                        } catch (InvocationTargetException e5) {
                            e5.printStackTrace();
                        } catch (ClassNotFoundException e6) {
                            e6.printStackTrace();
                        }



//                        try {
//                            Class.forName("android.os.SystemProperties")
//                                    .getDeclaredMethod("set",
//                                            new Class[]{String.class, String.class}).
//                                    invoke(null, new Object[]{"ctl.start", "nfsmanager"});
//                        } catch (NoSuchMethodException e1) {
//                            e1.printStackTrace();
//                        } catch (IllegalAccessException e3) {
//                            e3.printStackTrace();
//                        } catch (IllegalArgumentException e4) {
//                            e4.printStackTrace();
//                        } catch (InvocationTargetException e5) {
//                            e5.printStackTrace();
//                        } catch (ClassNotFoundException e6) {
//                            e6.printStackTrace();
//                        }
                    }
                } catch (Exception e7) {
                }
            } catch (IOException e8) {
                e8.printStackTrace();
            }
        }
    }

//    /**
//     * 执行sh脚本
//     * @param shPath
//     */
//    private static void cmdSh(String shPath){
//
//        String cmdstring = "chmod a+x "+shPath;
//        try {
//            Process proc = Runtime.getRuntime().exec(cmdstring);
//            proc.waitFor(); //阻塞，直到上述命令执行完
//            cmdstring = "bash "+shPath; //这里也可以是ksh等
//            proc = Runtime.getRuntime().exec(cmdstring);
//            // 注意下面的操作
//            String ls_1;
//            BufferedReader bufferedReader = new BufferedReader(
//                    new InputStreamReader(proc.getInputStream()));
//            while ( (ls_1=bufferedReader.readLine()) != null);
//            bufferedReader.close();
//            proc.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

    /**
     * 生成脚本
     * @param cmd
     * @return
     */
    private static boolean ShellFileWrite(String[] cmd) {
        File shell = new File(SHELL_PATH);
        if (shell.exists()) {
            shell.delete();
        }
        try {
            shell.createNewFile();
            Runtime.getRuntime().exec("chmod 777 /data/etc/nfsmanager.sh");
            try {
                BufferedWriter buffwr = new BufferedWriter(new FileWriter(shell));
                for (String str : cmd) {
                    buffwr.write(str);
                    buffwr.newLine();
                    buffwr.flush();
                }
                buffwr.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            return false;
        }
    }


    /**
     * 获取ip
     * @param context
     * @return
     */
    public static String getSelfAddress(Context context) {
        String wifi = null;
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                String name = netInterface.getName();
                String temp;
                if (name.equals("eth0") || name.equals("eth1")) {
                    if (cm.getNetworkInfo(9).isConnected()) {
                        temp = parseAddress(addresses);
                        if (temp != null) {
                            return temp;
                        }
                    } else {
                        continue;
                    }
                } else if (name.equals("wlan0") && cm.getNetworkInfo(1).isConnected()) {
                    temp = parseAddress(addresses);
                    if (temp != null) {
                        wifi = temp;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return wifi;
    }

    private static String parseAddress(Enumeration<InetAddress> addresses) {
        while (addresses.hasMoreElements()) {
            InetAddress ad = (InetAddress) addresses.nextElement();
            if (ad != null && (ad instanceof Inet4Address)) {
                return ad.getHostAddress();
            }
        }
        return null;
    }




    public static void sendPauseBroadCast(Context context) {
        Intent intent = new Intent("com.kaiboer.gl.mediaplayer.stopmusic.action");
        intent.putExtra("key", 0);
        context.sendBroadcast(intent);
        context.sendBroadcast(new Intent("com.kaiboer.fm.music_stop"));
    }

    public static boolean isAppSystemInstall(Context context, String pkgName) {
        try {
            return context.getPackageManager().getPackageInfo(pkgName, 0) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<String> getPalyList(String rootPath) {
        ArrayList<String> movie_list = new ArrayList();
        try {
            File file = new File(rootPath.substring(0, rootPath.lastIndexOf("/")));
            if (file.isDirectory()) {
                for (File p_file : file.listFiles()) {
                    String path = p_file.getAbsolutePath();
                    if (FileType.isType(2, p_file.getName())) {
                        movie_list.add(path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return movie_list;
    }

    @SuppressLint({"DefaultLocale"})
    public static String getIpInfo() {
        String ip = null;
        String temp = null;
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();
                Enumeration enumIpAddr = intf.getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        String name = intf.getName().toLowerCase();
                        if (name.equals("eth0") || name.equals("eth1")) {
                            return inetAddress.getHostAddress();
                        }
                        if (name.equals("wlan0")) {
                            ip = inetAddress.getHostAddress();
                        } else {
                            temp = inetAddress.getHostAddress();
                        }
                    }
                }
                if (!TextUtils.isEmpty(ip)) {
                    break;
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }
        return ip == null ? temp : ip;
    }

    @Deprecated
    public static String encodeCommand(String str) {
        return str;
    }

    @Deprecated
    public static String decodeCommand(String cmd) {
        return cmd;
    }

    public static String escapeSequence(String input) {
        if (input == null) {
            throw new NullPointerException();
        }
        String result = "";
        int length = input.length();
        for (int i = 0; i < length; i++) {
            char c = input.charAt(i);
            switch (c) {
                case ' ':
                case MotionEventCompat.AXIS_GENERIC_7 /*38*/:
                case MotionEventCompat.AXIS_GENERIC_8 /*39*/:
                case MotionEventCompat.AXIS_GENERIC_9 /*40*/:
                case MotionEventCompat.AXIS_GENERIC_10 /*41*/:
                case '`':
                    result = result + "\\" + c;
                    break;
                default:
                    result = result + c;
                    break;
            }
        }
        return result;
    }


    //播放
    public static final String SUPPORTED_VIDEOS = "_mp4_3gp_mkv_mov_avi_rmvb_wav_m3u8_";//mkv_mov_avi_rmvb_wav_m3u8_

    /**
     *
     * @param path
     * @return
     */
    public static final String getVideoMimeType(String path) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (TextUtils.isEmpty(extension)) {
            return null;
        }
        extension = extension.toLowerCase();
        if (!SUPPORTED_VIDEOS.contains(extension)) {
            return null;
        }
        return new StringBuilder("video/").append(extension).toString();
    }


    public static boolean isNfsUrl(String url){

        if (TextUtils.isEmpty(url)) {
            return false;
        }
        return url.trim().startsWith(NfsHelper.NFS_URL_LAN);
    }
}

