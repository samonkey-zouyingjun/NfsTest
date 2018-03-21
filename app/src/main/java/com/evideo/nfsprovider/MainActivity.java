package com.evideo.nfsprovider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.evideo.nfsprovider.manager.DefaultNfsManager;
import com.evideo.nfsprovider.model.NfsDevice;
import com.evideo.nfsprovider.scan.OnNfsSearchListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Used to load the 'nfs_client' library on application startup.
    static {
        System.loadLibrary("nfs_client");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void search(View view) {

        final DefaultNfsManager manager = new DefaultNfsManager(this);

        manager.setOnNfsSearchListener(new OnNfsSearchListener() {
            @Override
            public void OnNFSDeviceAddListener(NfsDevice nfsDevice) {
                Log.d(TAG, "OnNFSDeviceAddListener: "+nfsDevice.ip);
            }

            @Override
            public void onCompleteListener(int i, boolean z) {

            }

            @Override
            public void onNfsDeveceChangeListener(int i) {

            }

            @Override
            public void onNfsScanStart(int i) {

            }
        });


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: start");
                /*NativeNfsFacade facade = new NativeNfsFacade();
                facade.findService();*/
                manager.scanDevices();
            }
        };

        new Thread(runnable).start();
    }
}
