package com.hwq.spBle.exception.hanlder;

import android.content.Context;
import android.util.Log;

import com.hwq.spBle.exception.ConnectException;
import com.hwq.spBle.exception.GattException;
import com.hwq.spBle.exception.InitiatedException;
import com.hwq.spBle.exception.OtherException;
import com.hwq.spBle.exception.TimeoutException;

/**
 * 异常处理总类！！！
 *
 * 可以考虑添加BleManage，在操作失败后重复执行！！！
 */
public class DefaultBleExceptionHandler extends BleExceptionHandler {

    private static final String TAG = "BleExceptionHandler";
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG, e.getDescription());
    }
}
