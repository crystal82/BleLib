package com.knight.lib.ble.callback;

import com.knight.lib.ble.info.BleDeviceBean;

import java.util.List;

/**
 * 作者： hwq
 * 创建时间：2018/3/20 0:44
 * 描述：
 */

public interface BleScanCallback {

    void onScanStarted(boolean state);

    void onScanFinished(List<BleDeviceBean> bleDeviceList);

    void onLeScan(BleDeviceBean bleDeviceBean);

    //根据条件查找符合(mac,name,ssid范围)
    void onScanPassDevice(BleDeviceBean bleDeviceBean);
}
