package org.kitdroid.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by huiyh on 2016/4/18.
 */
public class StringUtils {

    @Nullable
    public static Integer parseInteger(String port) {
        if (!TextUtils.isEmpty(port) && TextUtils.isDigitsOnly(port)) {
            try {
                int i = Integer.parseInt(port);
                return i;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static int parseInt(String port, int defaultValue) {
        if (!TextUtils.isEmpty(port) && TextUtils.isDigitsOnly(port)) {
            try {
                int i = Integer.parseInt(port);
                return i;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return defaultValue;
    }
}
