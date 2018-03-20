package com.knight.lib.ble.exception;


public class SimpleException extends BleException {

    public SimpleException(String description) {
        super(ERROR_CODE_OTHER, description);
    }

}
