package org.kitdroid.helper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import org.kitdroid.proxyhelper.CustomApplication;

/**
 * Created by huiyh on 2016/4/18.
 */
public class ContextMate {
    private static Application sApplication;

    public static void init(Application application) {
        if (sApplication != null) {
            return;
        }
        sApplication = application;
    }

    public static Application getApplication() {
        return sApplication;
    }

    /**
     * name is global
     *
     * @return
     */
    public static SharedPreferences getPreferences() {
        return sApplication.getSharedPreferences("global", Context.MODE_PRIVATE);
    }
}
