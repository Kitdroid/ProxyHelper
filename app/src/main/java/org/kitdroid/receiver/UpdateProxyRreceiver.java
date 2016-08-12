package org.kitdroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.kitdroid.helper.Toaster;

/**
 * Created by baoyongzhang on 16/8/12.
 */
public class UpdateProxyRreceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toaster.showLong("onReceive");
    }
}
