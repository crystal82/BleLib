package com.knight.lib.ble.bleControl;

import com.knight.lib.ble.BleManager;

import java.util.UUID;

/**
 * 作者： hwq
 * 创建时间：2018/3/19 23:36
 * 描述：
 */

public class BleScanConfig {

    public UUID[]  mServiceUuids = null;
    public boolean mAutoConnect  = false;
    public long    mScanTime     = BleManager.DEFAULT_SCAN_TIME;

    public BleScanConfig() {
    }

    public BleScanConfig(Builder builder) {
        this.mServiceUuids = builder.mServiceUuids;
        this.mAutoConnect = builder.mAutoConnect;
        this.mScanTime = builder.mScanTime;
    }

    public static class Builder {

        private UUID[]  mServiceUuids = null;
        private boolean mAutoConnect  = false;
        private long    mScanTime     = BleManager.DEFAULT_SCAN_TIME;


        public void setServiceUuids(UUID[] serviceUuids) {
            mServiceUuids = serviceUuids;
        }

        public void setAutoConnect(boolean autoConnect) {
            mAutoConnect = autoConnect;
        }

        public void setScanTime(int scanTime) {
            mScanTime = scanTime;
        }

        public BleScanConfig build() {
            return new BleScanConfig(this);
        }
    }
}
