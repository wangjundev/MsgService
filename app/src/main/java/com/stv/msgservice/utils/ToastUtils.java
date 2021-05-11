package com.stv.msgservice.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {
    private static Handler handler = new Handler(Looper.getMainLooper());



    public static void showMessage(final Context context, final String msg) {
        showMessage(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showMessageLong(final Context context, final String msg) {
        showMessage(context, msg, Toast.LENGTH_LONG);
    }

    private static void showMessage(final Context act, final String msg,
                                    final int len) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(act,msg,len).show();
            }
        });
    }

}
