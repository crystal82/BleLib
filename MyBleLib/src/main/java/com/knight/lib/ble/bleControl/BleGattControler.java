package com.knight.lib.ble.bleControl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Build;
import android.os.Handler;

import com.knight.lib.ble.BleManager;
import com.knight.lib.ble.callback.BleWriteCallback;
import com.knight.lib.ble.exception.SimpleException;
import com.knight.lib.ble.info.BleConstant;

import java.util.UUID;

/**
 * 作者：HWQ on 2018/3/20 15:15
 * 描述：控制蓝牙BlueToothGatt进行读写操作
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleGattControler {
    private BleBluetoothControler       mBleBluetoothControler;
    private BluetoothGatt               mBluetoothGatt;
    private BluetoothGattService        mGattService;
    private BluetoothGattCharacteristic mCharacteristic;
    private Handler                     mHandler;

    public BleGattControler(BleBluetoothControler bleBluetoothControler) {
        this.mBleBluetoothControler = bleBluetoothControler;
        this.mBluetoothGatt = bleBluetoothControler.getBluetoothGatt();
        this.mHandler = new Handler();
    }

    private BleGattControler withUUID(UUID serviceUUID, UUID characteristicUUID) {
        if (serviceUUID != null && mBluetoothGatt != null) {
            mGattService = mBluetoothGatt.getService(serviceUUID);
        }
        if (mGattService != null && characteristicUUID != null) {
            mCharacteristic = mGattService
                    .getCharacteristic(characteristicUUID);
        }
        return this;
    }

    public BleGattControler withUUIDString(String serviceUUID, String
            characteristicUUID) {
        return withUUID(formUUID(serviceUUID), formUUID(characteristicUUID));
    }

    private UUID formUUID(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }

    public void writeCharacteristic(byte[] data, BleWriteCallback bleWriteCallback, String
            uuid_write) {
        if (data == null || data.length <= 0) {
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new SimpleException("the data to be " +
                                                                            "written is" +
                                                                            " empty"));
            return;
        }

        if (mCharacteristic == null
                || (mCharacteristic.getProperties()
                & (BluetoothGattCharacteristic.PROPERTY_WRITE |
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) == 0) {
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new SimpleException("this characteristic not support write!"));
            return;
        }

        if (mCharacteristic.setValue(data)) {
            writeMsgInit();
            mBleBluetoothControler.addWriteCallback(uuid_write, bleWriteCallback);
            mHandler.sendMessageDelayed(
                    mHandler.obtainMessage(BleConstant.MSG_CHA_WRITE_START,
                                           bleWriteCallback),
                    BleManager.getInstance().getOperateTimeout());

            if (!mBluetoothGatt.writeCharacteristic(mCharacteristic)) {
                writeMsgInit();
                if (bleWriteCallback != null)
                    bleWriteCallback.onWriteFailure(new SimpleException("gatt writeCharacteristic fail"));
            }
        } else {
            if (bleWriteCallback != null)
                bleWriteCallback.onWriteFailure(new SimpleException("Updates the " +
                                                                            "characteristic fail"));
        }
    }

    public void writeMsgInit() {
        mHandler.removeMessages(BleConstant.MSG_CHA_WRITE_START);
    }

}
