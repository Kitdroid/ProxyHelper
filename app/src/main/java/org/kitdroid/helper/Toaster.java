package org.kitdroid.helper;

import android.content.Context;
import android.widget.Toast;

import org.kitdroid.proxyhelper.BuildConfig;

/**
 * Created by 远航 on 2015/3/28.
 */
public class Toaster {

    public static void showLong(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    public static void showLong(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    public static void showShort(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showShort(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void showTest(CharSequence text) {
        if (!isOpen()) {
            return;
        }
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    private static final boolean isOpen() {
        return BuildConfig.DEBUG;
    }

    private static Context getContext() {
        return ContextMate.getApplication();
    }
}

