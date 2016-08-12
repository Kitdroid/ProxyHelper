package org.kitdroid.proxyhelper;

import android.app.Application;
import android.content.Context;
import android.view.ContextMenu;

import org.kitdroid.helper.ContextMate;

/**
 * Created by huiyh on 2016/4/18.
 */
public class CustomApplication extends Application {

    public CustomApplication() {
        ContextMate.init(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
