package org.kitdroid.helper;

import android.util.Log;

import org.kitdroid.proxyhelper.BuildConfig;

/**
 * Created by 远航 on 2015/3/28.
 */
public class Logger {

    private static final boolean isOpen() {
        return BuildConfig.DEBUG;
    }

    public static final void e(String tag, String msg) {
        if (!isOpen()) {
            return;
        }
        Log.e(tag, msg);
    }

    public static final void w(String tag, String msg) {
        if (!isOpen()) {
            return;
        }
        Log.w(tag, msg);
    }

    public static final void i(String tag, String msg) {
        if (!isOpen()) {
            return;
        }
        Log.i(tag, msg);
    }

    public static final void d(String tag, String msg) {
        if (!isOpen()) {
            return;
        }
        Log.d(tag, msg);
    }

    public static final void v(String tag, String msg) {
        if (!isOpen()) {
            return;
        }
        Log.v(tag, msg);
    }

    public static final void printStackTrace(Throwable t) {
        if (!isOpen()) {
            return;
        }
        t.printStackTrace();
    }
}
