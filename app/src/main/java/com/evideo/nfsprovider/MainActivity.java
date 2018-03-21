package com.evideo.nfsprovider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.evideo.nfsprovider.nativefacade.NativeNfsFacade;

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

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: start");
                NativeNfsFacade facade = new NativeNfsFacade();
                facade.findService();
            }
        };

        new Thread(runnable).start();
    }
}
