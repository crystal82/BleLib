package com.knight.lib.ble.callback;


import com.knight.lib.ble.exception.BleException;

public abstract class BleWriteCallback extends BleBaseCallback{

    public abstract void onWriteSuccess(byte[] writeInfo);

    public abstract void onWriteFailure(BleException exception);

}
