
package com.hwq.spBle.conn;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import com.hwq.spBle.exception.BleException;


public abstract class BleGattCallback extends BluetoothGattCallback {

    public abstract void onNotFoundDevice();

    public abstract void onFoundDevice(BluetoothDevice device);

    public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

    public abstract void onConnectDisconnected(BleException exception);

}