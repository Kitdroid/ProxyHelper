package org.kitdroid.util;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;

/**
 * 各种系统服务
 */
public class SystemUtils {

    /**
     * recommend default thread pool size according to system available processors, {@link #getDefaultThreadPoolSize()}
     **/
    public static final int DEFAULT_THREAD_POOL_SIZE = getDefaultThreadPoolSize();

    private SystemUtils() {
        throw new AssertionError();
    }

    /**
     * get recommend default thread pool size
     *
     * @return if 2 * availableProcessors + 1 less than 8, return it, else return 8;
     * @see {@link #getDefaultThreadPoolSize(int)} max is 8
     */
    public static int getDefaultThreadPoolSize() {
        return getDefaultThreadPoolSize(8);
    }

    /**
     * get recommend default thread pool size
     *
     * @param max
     * @return if 2 * availableProcessors + 1 less than max, return it, else return max;
     */
    public static int getDefaultThreadPoolSize(int max) {
        int availableProcessors = 2 * Runtime.getRuntime().availableProcessors() + 1;
        return availableProcessors > max ? max : availableProcessors;
    }

    /**
     * 剪贴板
     *
     * @param context
     * @return
     */
    @TargetApi(11)
    public static ClipboardManager getClipboardManager(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        return clipboardManager;
    }

    private static android.text.ClipboardManager getClipboardManagerOld(Context context) {
        return (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static void copyToClipboard(Context context, String content) {
        if (VERSION.SDK_INT >= 11) {
            ClipboardManager clipboardManager = getClipboardManager(context);
            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, content));
        } else {
            android.text.ClipboardManager clipboardManager = getClipboardManagerOld(context);
            clipboardManager.setText(content);
        }
    }

    /**
     * @param context
     * @return
     */
    public static NotificationManager getNotificationManager(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    /**
     * 网络信息
     *
     * @param context
     * @return
     */
    public static ConnectivityManager getConnectivityManager(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        return connMgr;
    }

    public static NetworkInfo getNetworkInfo(Context context) {
        NetworkInfo netInfo = getConnectivityManager(context).getActiveNetworkInfo();
        return netInfo;
    }

    /**
     * 电话相关信息
     *
     * @param context
     * @return
     */
    public static TelephonyManager getTelephonyManager(Context context) {
        TelephonyManager phoneMgr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return phoneMgr;
    }

    public static String getDriviceIMEI(Context context) {
        return getTelephonyManager(context).getDeviceId();
    }
}
