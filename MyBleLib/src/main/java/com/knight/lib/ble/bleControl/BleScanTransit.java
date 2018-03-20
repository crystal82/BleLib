package com.knight.lib.ble.bleControl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.knight.lib.ble.info.BleDeviceBean;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 作者： hwq
 * 创建时间：2018/3/19 22:47
 * 描述：自定义LeScanCallback，对扫描返回结果进行校验！显示或连接指定的设备
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleScanTransit implements BluetoothAdapter.LeScanCallback {

    private Handler             mMainHandler;
    private long                scanTime;
    private boolean             autoConnect;
    private UUID[]              serviceUuids;
    private List<BleDeviceBean> mBleDeviceList;

    public BleScanTransit() {
    }

    public BleScanTransit(long scanTime, boolean autoConnect, UUID[] serviceUuids) {
        this.scanTime = scanTime;
        this.autoConnect = autoConnect;
        this.serviceUuids = serviceUuids;

        mBleDeviceList = new ArrayList<>();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public abstract void onScanStarted(boolean state);

    public abstract void onScanFinished(List<BleDeviceBean> bleDeviceList);

    public abstract void onLeScan(BleDeviceBean bleDeviceBean);

    //根据条件查找符合(mac,name,ssid范围)
    public abstract void onScanPassDevice(BleDeviceBean bleDeviceBean);

    @Override
    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[]
            scanRecord) {

        final BleDeviceBean bleDevice = new BleDeviceBean(device, rssi, scanRecord, System
                .currentTimeMillis());
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onLeScan(bleDevice);
            }
        });

        checkLeScanDevice(bleDevice);
    }


    //TODO:在子线程中，根据条件校验扫描设备。（使用HandlerThread）
    public void checkLeScanDevice(BleDeviceBean bleDevice) {
        onScanPassDevice(bleDevice);
    }

    public void notifyScanStarted(boolean startScanSuccess) {
        if (startScanSuccess) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onScanFinished(mBleDeviceList);
                }
            }, scanTime);
        }
    }


    public void notifyScanStopped() {
        removeHandlerMsg();
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                onScanFinished(mBleDeviceList);
            }
        });
    }

    public final void removeHandlerMsg() {
        mMainHandler.removeCallbacksAndMessages(null);
    }

}
