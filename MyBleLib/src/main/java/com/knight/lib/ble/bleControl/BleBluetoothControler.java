package com.knight.lib.ble.bleControl;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.os.Build;

import com.knight.lib.ble.BleManager;
import com.knight.lib.ble.callback.BleConnectGattCallback;
import com.knight.lib.ble.callback.BleWriteCallback;
import com.knight.lib.ble.exception.ConnectException;
import com.knight.lib.ble.exception.GattActionException;
import com.knight.lib.ble.info.BleConnectState;
import com.knight.lib.ble.info.BleDeviceBean;
import com.knight.lib.ble.utils.BleLg;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 作者：HWQ on 2018/3/20 11:06
 * 描述：通过BluetoothDevice进行连接断开操作，以及存储蓝牙操作相关回调！
 */

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BleBluetoothControler {
    private BleDeviceBean mBleDeviceBean;
    private BleConnectState connectState = BleConnectState.CONNECT_IDLE;
    private BleConnectGattCallback bleGattCallback;
    private BluetoothGatt          bluetoothGatt;

    private HashMap<String, BleWriteCallback> bleWriteCallbackHashMap = new HashMap<>();

    public BleBluetoothControler(BleDeviceBean bleDeviceBean) {
        mBleDeviceBean = bleDeviceBean;
    }

    /**
     * 执行连接操作，保存当前BleGattCallback
     *
     * @param bleDeviceBean
     * @param autoConnect
     * @param bleGattCallback 连接回调，用于更新UI
     * @return
     */
    public BluetoothGatt connect(BleDeviceBean bleDeviceBean, boolean autoConnect,
                                 BleConnectGattCallback bleGattCallback) {

        setConnectGattCallback(bleGattCallback);

        Context context = BleManager.getInstance().getContext();
        BluetoothGatt bluetoothGatt = bleDeviceBean.getDevice().connectGatt(context,
                                                                            autoConnect,
                                                                            gattCallback);
        if (bluetoothGatt != null) {
            if (bleGattCallback != null)
                bleGattCallback.onStartConnect();
            connectState = BleConnectState.CONNECT_CONNECTING;
        }
        return bluetoothGatt;
    }

    public synchronized void setConnectGattCallback(BleConnectGattCallback callback) {
        bleGattCallback = callback;
    }

    public synchronized void removeConnectGattCallback() {
        bleGattCallback = null;
    }

    BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int
                newState) {
            super.onConnectionStateChange(gatt, status, newState);

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                //连接成功，查找服务
                gatt.discoverServices();

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                closeBluetoothGatt();
                BleManager.getInstance().setBleBluetoothControler(null);

                if (connectState == BleConnectState.CONNECT_CONNECTING) {
                    connectState = BleConnectState.CONNECT_FAILURE;
                    if (bleGattCallback != null) {
                        bleGattCallback.onConnectFail(new ConnectException(gatt, status));
                    }
                } else if (connectState == BleConnectState.CONNECT_CONNECTED) {
                    connectState = BleConnectState.CONNECT_DISCONNECT;
                    if (bleGattCallback != null) {
                        bleGattCallback.onDisConnected(mBleDeviceBean, gatt, status);
                    }
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                bluetoothGatt = gatt;
                connectState = BleConnectState.CONNECT_CONNECTED;

                bleGattCallback.onConnectSuccess(mBleDeviceBean, gatt, status);

                BleManager.getInstance().setBleBluetoothControler(
                        BleBluetoothControler.this);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            String           uuid             = characteristic.getUuid().toString();
            BleWriteCallback bleWriteCallback = bleWriteCallbackHashMap.get(uuid);
            if (bleWriteCallback != null) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    bleWriteCallback.onWriteSuccess(characteristic.getValue());
                } else {
                    bleWriteCallback.onWriteFailure(new GattActionException(status));
                }
            }
        }
    };


    public synchronized void disconnect(BleDeviceBean bleDevice) {
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
    }

    private synchronized void closeBluetoothGatt() {
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
    }

    public void destroy() {
        connectState = BleConnectState.CONNECT_IDLE;
        if (bluetoothGatt != null) {
            bluetoothGatt.disconnect();
        }
        if (bluetoothGatt != null) {
            refreshDeviceCache();
        }
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
        }
        removeConnectGattCallback();
        clearCharacterCallback();
    }

    public synchronized void clearCharacterCallback() {
        if (bleWriteCallbackHashMap != null) {
            bleWriteCallbackHashMap.clear();
        }
    }


    private synchronized boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                boolean success = (Boolean) refresh.invoke(getBluetoothGatt());
                BleLg.d("refreshDeviceCache, is success:  " + success);
                return success;
            }
        } catch (Exception e) {
            BleLg.d("exception occur while refreshing device: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public BleDeviceBean getDevice() {
        return mBleDeviceBean;
    }

    public BluetoothGatt getBluetoothGatt() {
        return bluetoothGatt;
    }

    public void addWriteCallback(String uuid_write, BleWriteCallback bleWriteCallback) {
        bleWriteCallbackHashMap.put(uuid_write, bleWriteCallback);
    }

    public void removeWriteCallback(String uuid_write) {
        bleWriteCallbackHashMap.remove(uuid_write);
    }
}
