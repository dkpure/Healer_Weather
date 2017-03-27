/*
 *
 * Copyright (c) 2016 Beijing Shuzijiayuan, All Rights Reserved.
 * Beijing Shuzijiayuan Confidential and Proprietary
 */
package com.sjxz.moji_weather.util;

import java.lang.reflect.Method;
import java.util.IllegalFormatException;
import java.util.Locale;

/**
 * Manages logging for the entire module.
 */
public class QLog {
    private static String TAG = "dkpure";
    private static final boolean DEBUGGABLE = isDebugVersion();
    public static final boolean DEBUG = isLoggable(android.util.Log.DEBUG);
    public static final boolean INFO = isLoggable(android.util.Log.INFO);
    public static final boolean VERBOSE = isLoggable(android.util.Log.VERBOSE);
    public static final boolean WARN = isLoggable(android.util.Log.WARN);
    public static final boolean ERROR = isLoggable(android.util.Log.ERROR);

    public static final int UNSPECIFIED = 0;
    public static final int ALWAYS_ON = 1;
    public static final int ALWAYS_OFF = 2;
    private static int sForceLogging = UNSPECIFIED;

    private QLog() {}

    public static void setLogTag(String tag) {
        TAG = tag;
    }

    public static void setForceLog(int force) {
        sForceLogging = force;
    }

    public static boolean isLoggable(int level) {
        if (sForceLogging == ALWAYS_ON) {
            return true;
        } else if (sForceLogging == ALWAYS_OFF) {
            return false;
        }
        if (DEBUGGABLE && level > android.util.Log.VERBOSE) {
            return true;
        }
        return android.util.Log.isLoggable(TAG, level);
    }

    public static void d(String prefix, String format, Object... args) {
        if (isLoggable(android.util.Log.DEBUG)) {
            android.util.Log.d(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void d(Object objectPrefix, String format, Object... args) {
        if (isLoggable(android.util.Log.DEBUG)) {
            android.util.Log.d(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void i(String prefix, String format, Object... args) {
        if (isLoggable(android.util.Log.INFO)) {
            android.util.Log.i(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void i(Object objectPrefix, String format, Object... args) {
        if (isLoggable(android.util.Log.INFO)) {
            android.util.Log.i(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void v(String prefix, String format, Object... args) {
        if (isLoggable(android.util.Log.VERBOSE)) {
            android.util.Log.v(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void v(Object objectPrefix, String format, Object... args) {
        if (isLoggable(android.util.Log.VERBOSE)) {
            android.util.Log.v(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void w(String prefix, String format, Object... args) {
        if (isLoggable(android.util.Log.WARN)) {
            android.util.Log.w(TAG, buildMessage(prefix, format, args));
        }
    }

    public static void w(Object objectPrefix, String format, Object... args) {
        if (isLoggable(android.util.Log.WARN)) {
            android.util.Log.w(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args));
        }
    }

    public static void e(String prefix, Throwable tr, String format, Object... args) {
        if (isLoggable(android.util.Log.ERROR)) {
            android.util.Log.e(TAG, buildMessage(prefix, format, args), tr);
        }
    }

    public static void e(Object objectPrefix, Throwable tr, String format, Object... args) {
        if (isLoggable(android.util.Log.ERROR)) {
            android.util.Log.e(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args),
                               tr);
        }
    }

    public static void wtf(String prefix, Throwable tr, String format, Object... args) {
        android.util.Log.wtf(TAG, buildMessage(prefix, format, args), tr);
    }

    public static void wtf(Object objectPrefix, Throwable tr, String format, Object... args) {
        android.util.Log.wtf(TAG, buildMessage(getPrefixFromObject(objectPrefix), format, args),
                tr);
    }

    public static void wtf(String prefix, String format, Object... args) {
        String msg = buildMessage(prefix, format, args);
        android.util.Log.wtf(TAG, msg, new IllegalStateException(msg));
    }

    public static void wtf(Object objectPrefix, String format, Object... args) {
        String msg = buildMessage(getPrefixFromObject(objectPrefix), format, args);
        android.util.Log.wtf(TAG, msg, new IllegalStateException(msg));
    }

    private static String getPrefixFromObject(Object obj) {
        return obj == null ? "<null>" : obj.getClass().getSimpleName();
    }

    private static String buildMessage(String prefix, String format, Object... args) {
        String msg;

        try {
            msg = (args == null || args.length == 0) ? format
                  : String.format(Locale.US, format, args);
        } catch (IllegalFormatException ife) {
            e("Log", ife, "IllegalFormatException: formatString='%s' numArgs=%d", format,
              args.length);
            msg = format + " (An error occurred while formatting the message.)";
        }

        return String.format(Locale.US, "%s: %s", prefix, msg);
    }

    private static boolean isDebugVersion() {
        boolean debuggable = false;
        try {
            Class SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method getIntMethod = SystemPropertiesClass.getMethod("getInt", String.class, int.class);
            debuggable = (int)getIntMethod.invoke(null, "ro.debuggable", 0) == 1;
        } catch (Exception e) {
            QLog.e(TAG, e, "isDebugVersion error.");
        }
        android.util.Log.w(TAG, "isDebugVersion, debuggable - " + debuggable);
        return debuggable;
    }
}
