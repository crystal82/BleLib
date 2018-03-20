package com.hwq.spBle;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.util.Log;

import com.hwq.spBle.conn.BleCharacterCallback;
import com.hwq.spBle.conn.BleGattCallback;
import com.hwq.spBle.exception.BleException;
import com.hwq.spBle.scan.ListScanCallback;
import com.hwq.spBle.utils.HexUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 代码示范
 */
public class BleDemoActivity extends Activity {

    // 下面的所有UUID及指令请根据实际设备替换
    private static final String UUID_SERVICE      = "";
    private static final String UUID_INDICATE     = "";
    private static final String UUID_NOTIFY       = "";
    private static final String UUID_WRITE        = "";
    private static final String SAMPLE_WRITE_DATA = "";                  // 要写入设备某一个character的指令

    private static final long   TIME_OUT     = 5000;                                          // 扫描超时时间
    private static final String DEVICE_NAME  = "这里写你的设备名";                         // 符合连接规则的蓝牙设备名
    private static final String CALLBACK_KEY = "回调函数关键字";                         // 符合连接规则的蓝牙设备名
    private static final String DEVICE_MAC   = "这里写你的设备地址";                        // 符合连接规则的蓝牙设备地址
    private static final String TAG          = "ble_sample";

    private BleManager bleManager;                                                      // Ble核心管理类

    private BluetoothDevice[] bluetoothDevices;

    AtomicBoolean mAtomicBoolean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_ble_demo);
        bleManager = new BleManager(this);
        bleManager.enableBluetooth();

    }

    /**
     * 判断是否支持ble
     */
    private boolean isSupportBle() {
        return bleManager.isSupportBle();
    }

    /**
     * 手动开启蓝牙
     */
    private void enableBlue() {
        bleManager.enableBluetooth();
    }

    /**
     * 手动关闭蓝牙
     */
    private void disableBlue() {
        bleManager.disableBluetooth();
    }

    /**
     * 刷新缓存操作
     */
    private void refersh() {
        bleManager.refreshDeviceCache();
    }

    /**
     * 关闭操作
     */
    private void close() {
        bleManager.closeBluetoothGatt();
    }

    /**
     * 扫描出周围所有设备
     */
    private void scanDevice() {
        bleManager.scanDevice(new ListScanCallback(TIME_OUT) {

            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                super.onLeScan(device, rssi, scanRecord);
                Log.i(TAG, "发现设备: " + device.getAddress());
            }

            @Override
            public void onDeviceFound(BluetoothDevice[] devices) {
                Log.i(TAG, "共发现" + devices.length + "台设备");
                for (int i = 0; i < devices.length; i++) {
                    Log.i(TAG, "name:" + devices[i].getName() + "------mac:" + devices[i].getAddress());
                }
                bluetoothDevices = devices;
            }

        });
    }

    /**
     * 当搜索到周围有设备之后，可以选择直接连某一个设备
     */
    private void connectDevice() {
        if (bluetoothDevices == null || bluetoothDevices.length < 1)
            return;
        BluetoothDevice sampleDevice = bluetoothDevices[0];

        bleManager.connectDevice(
                sampleDevice,
                CALLBACK_KEY,
                true,
                new BleGattCallback() {
                    @Override
                    public void onNotFoundDevice() {
                        Log.i(TAG, "未发现设备！");
                    }

                    @Override
                    public void onFoundDevice(BluetoothDevice device) {
                        Log.i(TAG, "发现设备: " + device.getAddress());
                    }

                    @Override
                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "连接成功！");
                        gatt.discoverServices();
                    }

                    @Override
                    public void onConnectDisconnected(BleException exception) {

                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "服务被发现！");
                        bleManager.getBluetoothState();
                    }

                });
    }

    /**
     * 扫描出周围指定名称设备、并连接
     */
    private void scanAndConnect() {
        bleManager.scanNameAndConnect(
                DEVICE_NAME,
                CALLBACK_KEY,
                TIME_OUT,
                false,
                new BleGattCallback() {
                    @Override
                    public void onNotFoundDevice() {
                        Log.i(TAG, "未发现设备！");
                    }

                    @Override
                    public void onFoundDevice(BluetoothDevice device) {
                        Log.i(TAG, "发现设备: " + device.getAddress());
                    }

                    @Override
                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "连接成功！");
                        gatt.discoverServices();
                    }

                    @Override
                    public void onConnectDisconnected(BleException exception) {
                        Log.i(TAG, "断开连接：" + exception.toString());
                        bleManager.handleException(exception);
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "服务被发现！");
                        bleManager.getBluetoothState();
                    }
                });
    }


    /**
     * 扫描出周围指定地址的设备、并连接
     */
    private void scanAndConnect2() {
        bleManager.scanMacAndConnect(
                DEVICE_MAC,
                CALLBACK_KEY,
                TIME_OUT,
                false,
                new BleGattCallback() {
                    @Override
                    public void onNotFoundDevice() {
                        Log.i(TAG, "未发现设备！");
                    }

                    @Override
                    public void onFoundDevice(BluetoothDevice device) {
                        Log.i(TAG, "发现设备: " + device.getAddress());
                    }

                    @Override
                    public void onConnectSuccess(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "连接成功！");
                        gatt.discoverServices();
                    }

                    @Override
                    public void onConnectDisconnected(BleException exception) {
                        Log.i(TAG, "连接失败或连接中断：" + exception.toString());
                        bleManager.handleException(exception);
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                        Log.i(TAG, "服务被发现！");
                        bleManager.getBluetoothState();
                    }
                });
    }

    /**
     * notify
     */
    private void listen_notify() {
        bleManager.notify(
                UUID_SERVICE,
                UUID_NOTIFY,
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "notify result： "
                                + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        bleManager.handleException(exception);
                    }
                });
    }

    /**
     * stop notify
     */
    private boolean stop_notify() {
        return bleManager.stopNotify(UUID_SERVICE, UUID_NOTIFY);
    }

    /**
     * indicate
     */
    private void listen_indicate() {
        bleManager.indicate(
                UUID_SERVICE,
                UUID_INDICATE,
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "indicate result： "
                                + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.e(TAG, "indicate: " + exception.toString());
                        bleManager.handleException(exception);
                    }
                });
    }

    /**
     * stop indicate
     */
    private boolean stop_indicate() {
        return bleManager.stopIndicate(UUID_SERVICE, UUID_INDICATE);
    }

    /**
     * write
     */
    private void write() {
        bleManager.writeDevice(
                UUID_SERVICE,
                UUID_WRITE,
                HexUtil.hexStringToBytes(SAMPLE_WRITE_DATA),
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "write result: "
                                + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.e(TAG, "write: " + exception.toString());
                        bleManager.handleException(exception);
                    }
                });
    }

    /**
     * read
     */
    private void read() {
        bleManager.readDevice(
                UUID_SERVICE,
                UUID_WRITE,
                new BleCharacterCallback() {
                    @Override
                    public void onSuccess(BluetoothGattCharacteristic characteristic) {
                        Log.d(TAG, "read result: "
                                + String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                    }

                    @Override
                    public void onFailure(BleException exception) {
                        Log.e(TAG, "read: " + exception.toString());
                        bleManager.handleException(exception);
                    }
                });
    }


}
