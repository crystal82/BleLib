package com.knight.lib.ble.callback;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.os.Build;

import com.knight.lib.ble.exception.BleException;
import com.knight.lib.ble.info.BleDeviceBean;

/**
 * 作者： hwq
 * 创建时间：2018/3/20 1:42
 * 描述：
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BleConnectGattCallback extends BluetoothGattCallback {

    public abstract void onStartConnect();

    public abstract void onConnectFail(BleException exception);

    public abstract void onConnectSuccess(BleDeviceBean bleDevice, BluetoothGatt gatt, int status);

    public abstract void onDisConnected( BleDeviceBean device,
                                        BluetoothGatt gatt, int status);

}
