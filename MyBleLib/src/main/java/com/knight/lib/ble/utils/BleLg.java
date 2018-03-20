package com.knight.lib.ble.utils;

/**
 * 作者： admin
 * 创建时间：2018/3/19 21:07
 * 描述：
 */

import android.text.TextUtils;
import android.util.Log;

/**
 * 打印LOG工具类
 */
public class BleLg {
    private static boolean sDebug = true;
    private static String  sTag   = "MyBleLib";

    public static void init(boolean debug, String tag) {
        BleLg.sDebug = debug;
        BleLg.sTag = tag;
    }

    private static String getFinalTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return sTag;
    }

    public static void d(String msg) {
        if (!sDebug) return;

        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(sTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + "   " + msg);
    }

    public static void d(String tag, String msg) {
        if (!sDebug) return;

        String            finalTag                = getFinalTag(tag);
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.d(finalTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + "   " + msg);
    }

    public static void e(String msg) {
        if (!sDebug) return;

        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(sTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + "   " + msg);
    }

    public static void e(String tag, String msg) {
        if (!sDebug) return;

        String            finalTag                = getFinalTag(tag);
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(finalTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + "   " + msg);
    }

    public static void i(String tag, String msg) {
        if (!sDebug) return;

        String            finalTag                = getFinalTag(tag);
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        Log.e(finalTag, "(" + targetStackTraceElement.getFileName() + ":"
                + targetStackTraceElement.getLineNumber() + ")" + "   " + msg);
    }


    //得到上一个栈帧
    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement   targetStackTrace = null;
        boolean             shouldTrace      = false;
        StackTraceElement[] stackTrace       = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(BleLg.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}