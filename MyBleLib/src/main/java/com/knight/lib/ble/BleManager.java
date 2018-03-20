
package com.knight.lib.ble;

import android.annotation.TargetApi;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import com.knight.lib.ble.bleControl.BleBluetoothControler;
import com.knight.lib.ble.bleControl.BleGattControler;
import com.knight.lib.ble.bleControl.BleScanConfig;
import com.knight.lib.ble.bleControl.BleScanControler;
import com.knight.lib.ble.callback.BleConnectGattCallback;
import com.knight.lib.ble.callback.BleScanCallback;
import com.knight.lib.ble.callback.BleWriteCallback;
import com.knight.lib.ble.exception.BleException;
import com.knight.lib.ble.info.BleDeviceBean;
import com.knight.lib.ble.utils.BleLg;

import java.util.UUID;

/**
 * 作者： hwq
 * 创建时间：2018/3/19 21:12
 * 描述：
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleManager {
    //全局操作，使用Application
    private Application mAppContext;
    private static BleManager sBleManager = new BleManager();

    //当前支持单个连接
    private BleBluetoothControler mBleBluetoothControler;

    //扫描时间间隔
    public static final  long DEFAULT_SCAN_TIME              = 10000;
    //最大连接数
    private static final int  DEFAULT_MAX_MULTIPLE_DEVICE    = 7;
    //操作超时时间
    private static final long DEFAULT_OPERATION_TIMEOUT_TIME = 5000;
    //数据包长度
    private static final int  DEFAULT_MTU                    = 23;
    private static final int  DEFAULT_MAX_MTU                = 512;
    //数据分包长度
    private static final int  DEFAULT_WRITE_DATA_SPLIT_COUNT = 20;
    private BluetoothAdapter mBluetoothAdapter;
    private BleScanConfig    mScanConfig;
    private BleScanControler mBleScanControler;

    //单例模式初始化
    private BleManager() {
    }

    public static BleManager getInstance() {
        return sBleManager;
    }

    public static BleManager getInstanceAndInit(Application app) {
        sBleManager.init(app);
        return sBleManager;
    }

    public void setBleBluetoothControler(BleBluetoothControler mBleBluetoothControler) {
        this.mBleBluetoothControler = mBleBluetoothControler;
    }

    public void init(Application app) {
        if (mAppContext == null && app != null) {
            mAppContext = app;

            BluetoothManager bluetoothManager = (BluetoothManager) mAppContext
                    .getSystemService
                            (Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                mBluetoothAdapter = bluetoothManager.getAdapter();
                //初始化为默认状态
                mScanConfig = new BleScanConfig();
                mBleScanControler = BleScanControler.getInstance();
            }
        }
    }

    public void scan(BleScanCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("BleScanCallback can not be Null!");
        }

        if (!isBlueEnable()) {
            BleLg.e("Bluetooth not enable!");
            callback.onScanStarted(false);
            return;
        }

        //通过scan控制器进行操作,初始化时进行
        long    scanTime     = mScanConfig.mScanTime;
        boolean autoConnect  = mScanConfig.mAutoConnect;
        UUID[]  serviceUuids = mScanConfig.mServiceUuids;
        mBleScanControler.doScan(scanTime, autoConnect, serviceUuids, callback);
    }

    public void stopScan() {
        mBleScanControler.stopLeScan();
    }

    public BluetoothGatt connect(BleDeviceBean bleDeviceBean, BleConnectGattCallback
            bleGattCallback) {
        if (bleGattCallback == null) {
            throw new IllegalArgumentException("BleConnectGattCallback null!");
        }

        if (!isBlueEnable()) {
            BleLg.e("Bluetooth not enable!");
            bleGattCallback.onConnectFail(new BleException(BleException.ERROR_CODE_OTHER,
                                                           "Bluetooth not enable!"));
            return null;
        }

        if (mBleBluetoothControler != null) {
            BleLg.e("Bluetooth not enable!");
            bleGattCallback.onConnectFail(new BleException(BleException.ERROR_CODE_OTHER,
                                                           "Support for connecting only" +
                                                                   " one device.!"));
            return null;
        }

        if (bleDeviceBean == null || bleDeviceBean.getDevice() == null) {
            bleGattCallback.onConnectFail(new BleException(BleException.ERROR_CODE_OTHER,
                                                           "Bluetooth Device is null!"));
            return null;
        } else {
            BleBluetoothControler bleBluetooth = new BleBluetoothControler(bleDeviceBean);
            return bleBluetooth.connect(bleDeviceBean, mScanConfig.mAutoConnect,
                                        bleGattCallback);
        }
    }

    public void disconnect(BleDeviceBean bleDevice) {
        if (mBleBluetoothControler != null) {
            mBleBluetoothControler.disconnect(bleDevice);
        }
    }
    public void write(BleDeviceBean bleDevice,
                      String uuid_service,
                      String uuid_write,
                      byte[] data,
                      BleWriteCallback callback) {

        BleGattControler bleGattControler = new BleGattControler(mBleBluetoothControler);
        bleGattControler.withUUIDString(uuid_service, uuid_write)
                .writeCharacteristic(data, callback, uuid_write);

    }

    //TODO:
    public void read() {
    }

    //TODO:
    public void setNotification() {
    }

    //TODO:
    public void setDescriber() {
    }

    //TODO:
    public void destroy() {
        mBleBluetoothControler.destroy();
    }

    //TODO:
    public void removeCallBack() {
        mBleBluetoothControler.removeConnectGattCallback();
    }

    public void blueEnable() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.enable();
        }
    }

    public boolean isBlueEnable() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public Context getContext() {
        return mAppContext;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return mBluetoothAdapter;
    }

    public BleScanControler getBleScanner() {
        return mBleScanControler;
    }

    public BleScanConfig getScanRuleConfig() {
        return mScanConfig;
    }

    public void setScanConfig(BleScanConfig scanConfig) {
        this.mScanConfig = scanConfig;
    }

    public long getOperateTimeout() {
        return DEFAULT_OPERATION_TIMEOUT_TIME;
    }
}
