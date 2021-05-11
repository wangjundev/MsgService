package com.stv.msgservice.app;

import android.app.ActivityManager;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.lqr.emoji.LQREmotionKit;
import com.stv.msgservice.datamodel.constants.Config;
import com.stv.msgservice.utils.UIUtils;

import java.io.File;

public class MyApp extends BaseApp {


    // 一定记得替换为你们自己的，ID请从BUGLY官网申请。关于BUGLY，可以从BUGLY官网了解，或者百度。
    public static String BUGLY_ID = "34490ba79f";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化表情控件
        UIUtils.application = this;
        LQREmotionKit.init(this, (context, path, imageView) -> Glide.with(context).load(path).apply(new RequestOptions().centerCrop().diskCacheStrategy(DiskCacheStrategy.RESOURCE).dontAnimate()).into(imageView));
        setupMsgDirs();
//        AppService.validateConfig(this);
//
//        // bugly，务必替换为你自己的!!!
//        if ("wildfirechat.cn".equals(Config.IM_SERVER_HOST)) {
//            CrashReport.initCrashReport(getApplicationContext(), BUGLY_ID, false);
//        }
//        // 只在主进程初始化，否则会导致重复收到消息
//        if (getCurProcessName(this).equals(BuildConfig.APPLICATION_ID)) {
//            // 如果uikit是以aar的方式引入 ，那么需要在此对Config里面的属性进行配置，如：
//            // Config.IM_SERVER_HOST = "im.example.com";
//            WfcUIKit wfcUIKit = WfcUIKit.getWfcUIKit();
//            wfcUIKit.init(this);
//            wfcUIKit.setAppServiceProvider(AppService.Instance());
//            PushService.init(this, BuildConfig.APPLICATION_ID);
//            MessageViewHolderManager.getInstance().registerMessageViewHolder(LocationMessageContentViewHolder.class, R.layout.conversation_item_location_send, R.layout.conversation_item_location_send);
//            setupWFCDirs();
//        }
    }

    private void setupMsgDirs() {
        File file = new File(Config.VIDEO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.AUDIO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.FILE_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(Config.PHOTO_SAVE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static String getCurProcessName(Context context) {

        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {

            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
