package com.evideo.nfsprovider.scan;

import com.evideo.nfsprovider.model.NfsDevice;

/**
 * Created by zouyingjun on 2018/3/21.
 */

public interface OnNfsSearchListener {
    void OnNFSDeviceAddListener(NfsDevice nfsDevice);

    void onCompleteListener(int i, boolean z);

    void onNfsDeveceChangeListener(int i);

    void onNfsScanStart(int i);
}
