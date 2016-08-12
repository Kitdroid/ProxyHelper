package org.kitdroid.helper;

/**
 * Created by huiyh on 2016/4/19.
 */
public class Stater {

    public static void onEvent(String pintId, String msg) {
        Logger.i("Stater", String.format("{id:\"%s\",msg:\"%s\"}", pintId, msg));
    }
}
