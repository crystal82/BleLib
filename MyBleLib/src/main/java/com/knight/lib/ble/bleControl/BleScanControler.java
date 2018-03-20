package com.knight.lib.ble.bleControl;

import android.annotation.TargetApi;
import android.os.Build;

import com.knight.lib.ble.BleManager;
import com.knight.lib.ble.callback.BleScanCallback;
import com.knight.lib.ble.info.BleDeviceBean;
import com.knight.lib.ble.info.BleScanState;

import java.util.List;
import java.util.UUID;

/**
 * 作者： hwq
 * 创建时间：2018/3/19 22:46
 * 描述：蓝牙扫描操作工具类
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleScanControler {

    private static BleScanControler sBleScanControler = new BleScanControler();
    private BleScanTransit mBleScanTransit;

    private BleScanControler() {}

    public static BleScanControler getInstance() {
        return sBleScanControler;
    }

    private BleScanState scanState = BleScanState.STATE_IDLE;

    public void doScan(long scanTime, boolean autoConnect, UUID[] serviceUuids, final BleScanCallback
            callback) {

        //控制反转ioc,由容器传入
        startLeScan(new BleScanTransit(scanTime, autoConnect, serviceUuids) {
            @Override
            public void onScanStarted(boolean state) {
                if (callback != null) {
                    callback.onScanStarted(state);
                }
            }

            @Override
            public void onScanFinished(List<BleDeviceBean> bleDeviceList) {
                if (callback != null) {
                    callback.onScanFinished(bleDeviceList);
                }
            }

            @Override
            public void onLeScan(BleDeviceBean bleDeviceBean) {
                if (callback != null) {
                    callback.onLeScan(bleDeviceBean);
                }
            }

            @Override
            public void onScanPassDevice(BleDeviceBean bleDeviceBean) {
                if (callback != null) {
                    callback.onScanPassDevice(bleDeviceBean);
                }
            }
        });
    }

    private synchronized void startLeScan(BleScanTransit bleScanTransit) {
        if (bleScanTransit == null)
            return;

        mBleScanTransit = bleScanTransit;
        boolean success = BleManager.getInstance().getBluetoothAdapter().startLeScan
                (mBleScanTransit);
        scanState = success ? BleScanState.STATE_SCANNING : BleScanState.STATE_IDLE;
        mBleScanTransit.notifyScanStarted(success);
    }

    public synchronized void stopLeScan() {
        if (mBleScanTransit == null)
            return;

        BleManager.getInstance().getBluetoothAdapter().stopLeScan(mBleScanTransit);
        scanState = BleScanState.STATE_IDLE;
        mBleScanTransit.notifyScanStopped();
    }

    public BleScanState getScanState() {
        return scanState;
    }
}
