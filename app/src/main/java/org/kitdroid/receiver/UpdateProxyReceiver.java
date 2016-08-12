package org.kitdroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import org.kitdroid.helper.Toaster;
import org.kitdroid.proxyhelper.proxy.ProxyEntity;
import org.kitdroid.proxyhelper.wifi.WiFiProxyManager;

/**
 * Created by baoyongzhang on 16/8/12.
 */
public class UpdateProxyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String host = intent.getStringExtra("host");
        int port = intent.getIntExtra("port", 80);
        if (!TextUtils.isEmpty(host)) {
            ProxyEntity entity = new ProxyEntity();
            entity.setHost(host);
            entity.setPort(port);
            WiFiProxyManager.getInstance().setProxy(entity);
        } else {
            WiFiProxyManager.getInstance().setProxy(null);
        }
    }
}
