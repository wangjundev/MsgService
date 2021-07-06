package com.stv.msgservice.datamodel.network;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtil {
    private static final int NOTIFICATION_MUSIC_ID = 10000;
    private static NotificationManager notificationManager;

    //初始化NotificationManager
    public static void initNotificationManager(Context context){
        if (notificationManager == null){
            notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        }
        //判断是否为8.0以上：Build.VERSION_CODES.O为26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道ID
            String channelId = "5gMsgNotification";
            Log.i("Junwang", "create notification channelId.");
            //创建通知渠道名称
            String channelName = "5g消息通知栏";
            //创建通知渠道重要性
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(context, channelId, channelName, importance);
        }
    }

    //创建通知渠道
    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        //channel有很多set方法
        //为NotificationManager设置通知渠道
        notificationManager.createNotificationChannel(channel);
    }

    public static String createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //创建通知渠道ID
            String channelId = "5gMsgNotification";
            //创建通知渠道名称
            String channelName = "5g消息通知栏";
            //创建通知渠道重要性
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelDescription = "5g消息通知描述"; //设置描述 最长30字符


            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, channelImportance);
            notificationChannel.setDescription(channelDescription);
            // 允许通知使用震动，默认为false
            notificationChannel.enableVibration(true);
            // 设置显示模式
            notificationChannel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

            return channelId;
        } else {
            return null;
        }
    }
}


