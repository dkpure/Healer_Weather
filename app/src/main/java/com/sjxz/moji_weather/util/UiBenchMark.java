package com.sjxz.moji_weather.util;

import android.os.SystemClock;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UiBenchMark {
    private static final String TAG = UiBenchMark.class.getSimpleName();

    private static final Map<String, Long> BenchMark = new ConcurrentHashMap<String, Long>();

    public static void mark(final String msg) {
        BenchMark.put(msg, SystemClock.uptimeMillis());
    }

    public static void dump(final String msg) {
        Long start = BenchMark.remove(msg);
        if (start != null) {
            final long elapsed = SystemClock.uptimeMillis() - start;

            Log.v(TAG, msg + " elapsed: " + elapsed + "ms");
        } else {
            Log.d(TAG, "Unknown bench mark msg - " + msg);
        }
    }
}
