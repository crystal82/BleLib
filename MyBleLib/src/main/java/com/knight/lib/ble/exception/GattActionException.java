package com.knight.lib.ble.exception;


public class GattActionException extends BleException {

    private int gattStatus;

    public GattActionException(int gattStatus) {
        super(ERROR_CODE_GATT, "Gatt Exception Occurred! ");
        this.gattStatus = gattStatus;
    }

    public GattActionException(int gattStatus, String errorDes) {
        super(ERROR_CODE_GATT, errorDes);
        this.gattStatus = gattStatus;
    }

    public int getGattStatus() {
        return gattStatus;
    }

    public GattActionException setGattStatus(int gattStatus) {
        this.gattStatus = gattStatus;
        return this;
    }

    @Override
    public String toString() {
        return "GattException{" +
                "gattStatus=" + gattStatus +
                "} " + super.toString();
    }
}
