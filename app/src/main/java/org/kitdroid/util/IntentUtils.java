package org.kitdroid.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.kitdroid.helper.Logger;

import java.util.List;

/**
 * Created by huiyh on 14-11-11.
 */
public class IntentUtils {

    private static final String TAG = IntentUtils.class.getSimpleName();

    /**
     * 判断Intent有没有对应的Activity去处理
     *
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentResolvable(Context context, Intent intent) {
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    /**
     * 判断Intent有没有对应的Activity去处理(本应用内),包含相应信息
     *
     * @param context
     * @param action
     * @return
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        i("Query intent activities count = " + list.size());
        for (ResolveInfo res : list) {
            i("Resolve package name = " + res.activityInfo.packageName);
        }

        return list.size() > 0;
    }

    public static boolean startQQChat(Context context, String qqNum) {
        StringBuilder builder = new StringBuilder();
        builder.append("mqqwpa://im/chat?chat_type=wpa&uin=").append(qqNum).append("&version=1");
        Uri uri = Uri.parse(builder.toString());

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (!isIntentResolvable(context, intent)) {
            return false;
        }
        context.startActivity(intent);
        return true;
    }


    public static void startWebBrower(Context context, String strUrl) {
        Uri uri = Uri.parse(strUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


    /**
     * 到拨号界面
     *
     * @param context
     * @param phoneNum
     */
    public static void startPhoneDial(Context context, String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            i("phoneNum is Empty");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    /**
     * 直接拨打
     *
     * @param context
     * @param phoneNum
     */
    public static void startPhoneCall(Context context, String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            i("phoneNum is Empty");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNum));
        context.startActivity(intent);
    }

    public static void startActivity(@NonNull Activity activity, @NonNull Class<? extends Activity> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public static void startActivityForResult(@NonNull Activity activity, @NonNull Class<? extends Activity> cls, int requestCode) {
        Intent intent = new Intent(activity, cls);
        activity.startActivityForResult(intent, requestCode);
    }

    private static void i(String msg) {
        Logger.i(TAG, msg);
    }
}
