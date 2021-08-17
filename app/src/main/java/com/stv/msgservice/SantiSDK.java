package com.stv.msgservice;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;

import com.stv.msgservice.ui.conversation.ConversationFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SantiSDK {
    public static final int STRINGTOKEN = -1;
    public static void init(Activity activity, Class smsreceiver, Class mmsreceiver){
        requestPermission(activity);
        final PackageManager packageManager = activity.getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(activity, /*SmsReceiver.class*/smsreceiver),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(
                new ComponentName(activity, /*MmsWapPushReceiver.class*/mmsreceiver),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void init(Activity activity, Class mmsreceiver){
        requestPermission(activity);
        final PackageManager packageManager = activity.getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(activity, /*MmsWapPushReceiver.class*/mmsreceiver),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static void addSantiFragment(AppCompatActivity activity){
        ConversationFragment conversationFragment = new ConversationFragment();
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrameLayout, conversationFragment, "content")
                .commit();
    }

    public static void requestPermission(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_MMS}, 1);

        }else{

        }
    }
}
