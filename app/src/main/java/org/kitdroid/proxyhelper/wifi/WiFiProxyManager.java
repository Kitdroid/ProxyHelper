package org.kitdroid.proxyhelper.wifi;

import android.content.Context;

import org.kitdroid.helper.ContextMate;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huiyh on 2016/4/18.
 */
public class WiFiProxyManager {

    private static WiFiProxyManager sInstance;

    private final Context mContext = ContextMate.getApplication();

    public static WiFiProxyManager getInstance() {
        if (sInstance == null) {
            synchronized (WiFiProxyManager.class) {
                if (sInstance == null) {
                    sInstance = new WiFiProxyManager();
                }
            }
        }
        return sInstance;
    }

    private WiFiProxyManager() {
    }

    public void getCurrentProxy() {

    }
}
