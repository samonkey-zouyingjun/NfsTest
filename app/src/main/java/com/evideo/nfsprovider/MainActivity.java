package com.evideo.nfsprovider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.evideo.nfsprovider.manager.DefaultNfsManager;
import com.evideo.nfsprovider.model.NfsDevice;
import com.evideo.nfsprovider.model.NfsFolder;
import com.evideo.nfsprovider.scan.OnNfsSearchListener;
import com.evideo.nfsprovider.util.NfsFileUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    String mIp;

    static {
        System.loadLibrary("nfs_client");
    }


    private ListView mListView,mListView2;
    private List<String> mServices = new ArrayList<>();
    private List<String> mFiles = new ArrayList<>();
    private ArrayAdapter<String> mAdapter,mAdapter2;
    private TextView mTvPath;
    private DefaultNfsManager mManager;
//    private Button mBtn;
    //设备共享目录
    private ArrayList<NfsFolder> mNfsFolders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.ls);
        mListView2 = (ListView) findViewById(R.id.ls2);
        mTvPath = (TextView) findViewById(R.id.tv_path);
//        mBtn = (Button) findViewById(R.id.openFile);
//        mBtn.setText("openDir:"+mCurrentPath);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mServices);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mIp = mServices.get(position);
                mTvPath.setText(mIp);
            }
        });

        mAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mFiles);
        mListView2.setAdapter(mAdapter2);
        mListView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String path = mFiles.get(position);

                if(!path.startsWith("nfs")){
                    path = "nfs://"+path;
                }

                mTvPath.setText(path);

                if(path.endsWith(".mp4")){
                    openMp4(path);
                }else {
                    //todo 判断是否文件夹
                    openDir(null);
                }


//                File file = new File(path);
//
//                if(!file.exists()){
//                    Toast.makeText(MainActivity.this, "文件不存在！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                if(file.isDirectory()){
//                    Toast.makeText(MainActivity.this, "文件夹！", Toast.LENGTH_SHORT).show();
//
//                }
//
//                if(file.isFile()){
//                    Toast.makeText(MainActivity.this, "打开文件！", Toast.LENGTH_SHORT).show();
//                }

            }
        });

    }

    private static final String T2 = "zouyingjun";

    /**
     * 打开并播放MP4
     * @param path
     */
    private void openMp4(String path) {

        Log.d(T2, "openMp4 NfsUrl: "+path);

        String httpUrl = parseNfsToHttpUrl(path);

        String mime = NfsFileUtils.getVideoMimeType(path);

        Log.d(T2, "openMp4 httpUrl: "+httpUrl+" mime:"+mime);

        if (!String.valueOf(mime).toLowerCase().startsWith("video")) {
            Toast.makeText(this, "NOT a video file  " + mime, Toast.LENGTH_SHORT).show();
            return;
        }


    }

    /**
     * 解析得到http url
     * @param path
     * @return
     */
    private String parseNfsToHttpUrl(String path) {

        return null;
    }

    /**
     * 是否是共享根目录
     * @param path
     * @return
     */
    private boolean isShareRoot(String path){
        if(TextUtils.isEmpty(path)){
            Log.e(TAG, "isShareRoot: path null" );
            return false;
        }

        for (NfsFolder f:
        mNfsFolders) {
            if(path.equals("nfs://"+f.ip+f.getPath())){
                return true;
            }
        }

        return false;
    }

    /**
     * search
     *
     * @param view
     */
    public void search(View view) {

        Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();

        mManager = new DefaultNfsManager(this);

        mManager.setOnNfsSearchListener(new OnNfsSearchListener() {
            @Override
            public void OnNFSDeviceAddListener(final NfsDevice nfsDevice) {
                Log.d(TAG, "OnNFSDeviceAddListener: " + nfsDevice.ip);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String tip = nfsDevice.ip;
                        if (!mServices.contains(tip)) {
                            mServices.add(nfsDevice.ip);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onCompleteListener(int i, boolean z) {

//                Log.d(TAG, "onCompleteListener: " + i + " _ " + z);
            }

            @Override
            public void onNfsDeveceChangeListener(int i) {
//                Log.d(TAG, "onNfsDeveceChangeListener: " + i);
            }

            @Override
            public void onNfsScanStart(int i) {
//                Log.d(TAG, "onNfsScanStart: " + i);
            }
        });

        mManager.scanDevices();
    }

    //打开设备
    public void openDevice(View view) {
        if (mIp == null) {
            return;
        }
        // nfs://server/path/file?argv=val[&arg=val]*
        Toast.makeText(this, "openDevice", Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mNfsFolders = mManager.openDevice(new NfsDevice(mIp));
                if (mNfsFolders != null && mNfsFolders.size()!=0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTvPath.setText(mNfsFolders.get(0).ip);

                            mFiles.clear();

                            for (NfsFolder f:mNfsFolders) {
                                mFiles.add(f.ip+f.getPath());
                            }
                            mAdapter2.notifyDataSetChanged();

                        }
                    });
                } else {
                    Log.e(TAG, "openDir: null");
                }
            }
        }).start();
    }

    /**
     * 打开文件夹
     * @param view
     */
    public void openDir(View view) {

        Toast.makeText(this, "openDir", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> strings = openDirSync2(mTvPath.getText().toString());

                mFiles.clear();

                Log.d(TAG, "run: ");
                if(strings != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String s:strings) {

                                Log.d("zouyingjun", "run: "+s);
                                mFiles.add(mTvPath.getText().toString()+"/"+s);
                            }
                            mAdapter2.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 同步打开文件夹
     * @param path
     */
    private native List<String> openDirSync2(String path);

    public void backPress(View view) {
        onBackPressed();
    }

//    public void mount(View view) {
//        Toast.makeText(this, "mount", Toast.LENGTH_SHORT).show();
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "mount start:");
//                boolean b = mManager.mountNfs(NfsFileUtils.sNfsFolder.ip,
//                        NfsFileUtils.sNfsFolder.getPath(), NfsFileUtils.sNfsFolder.ip);
//
//                Log.d(TAG, "mount issucceed:"+b);
//            }
//        }).start();
//    }
//
//
//    public void openFile(View v){
////        String mountedPoint = NfsFileUtils.sNfsFolder.getMountedPoint();
//
////        openFileDir(mCurrentPath);
//
//    }

//    private String mCurrentPath = "/sdcard";
//    private void openFileDir(String mountedPoint) {
//
//
//        Log.d(TAG, "openFileDir: "+mountedPoint);
//        if(TextUtils.isEmpty(mountedPoint)){
//            Toast.makeText(this, "找不到路径", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        File file = new File(mountedPoint);
//
//        if(file.exists()&&file.isDirectory()){
//            mAdapter2.clear();
////            mCurrentPath = mountedPoint;
//
////            mBtn.setText(mCurrentPath);
//
//            String[] list = file.list();
//            if (list.length ==0){
//                Log.d(TAG, "openFileDir: list is null!");
//            }
//            for (String name:
//                    list) {
//                Log.d(TAG, "openFileDir name: "+name);
//                mFiles.add(mountedPoint+"/"+name);
//            }
//            mAdapter2.notifyDataSetChanged();
//        }else {
//            Log.e(TAG, "openFileDir: failed!" );
//        }
//    }

    public void backFolder(View view) {
        String currentPath = mTvPath.getText().toString();
        if(isShareRoot(currentPath)){
            return;
        }
        Toast.makeText(this, "上一级", Toast.LENGTH_SHORT).show();
        String substring = currentPath.substring(0, currentPath.lastIndexOf("/"));
        mTvPath.setText(substring);
        openDir(null);
    }
}
