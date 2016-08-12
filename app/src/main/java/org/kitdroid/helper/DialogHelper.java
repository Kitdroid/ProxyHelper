package org.kitdroid.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;

/**
 * Created by huiyh on 2016/4/18.
 */
public class DialogHelper {

    public static Dialog showConfirmDialog(Activity activity, String msg, DialogInterface.OnClickListener listener) {

        Builder builder = new Builder(activity);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", listener);
        builder.setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }

    public static Dialog showWarnDialog(Activity activity, String msg, DialogInterface.OnClickListener listener) {

        Builder builder = new Builder(activity);
        builder.setTitle("提示");
        builder.setMessage(msg);
        builder.setPositiveButton("确定", listener);

        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }
}
