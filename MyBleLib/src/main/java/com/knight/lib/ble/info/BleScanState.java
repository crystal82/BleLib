package com.knight.lib.ble.info;

/**
 * 作者： hwq
 * 创建时间：2018/3/19 22:54
 * 描述：
 */

public enum BleScanState {

    STATE_IDLE(-1),
    STATE_SCANNING(0X01);

    private int code;

    BleScanState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}