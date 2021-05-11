package com.stv.msgservice.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.stv.msgservice.datamodel.constants.MessageConstants;

import java.util.List;

public class NativeFunctionUtil {
    public static final String URL = "url";
    public static final String TITLE = "title";

    public static void callNativeFunction(int functionNo, Context context, String copyText, View targetView, String phoneNumber){
        switch (functionNo){
            case MessageConstants.NativeActionType.PHONE_CALL:
                callNumber(context, targetView, phoneNumber);
                break;
            case MessageConstants.NativeActionType.SEND_MSG:
                sendSMS(context, phoneNumber);
                break;
            case MessageConstants.NativeActionType.TAKE_PICTURE:
                takePicture(context);
                break;
            case MessageConstants.NativeActionType.TAKE_VIDEO:
                takeVideo(context);
                break;
            case MessageConstants.NativeActionType.COPY:
                copyText(context, copyText);
                break;
            case MessageConstants.NativeActionType.OPEN_LOCATION:
                break;
            case MessageConstants.NativeActionType.CALENDAR:
                break;
            case MessageConstants.NativeActionType.READ_CONTACT:
                break;
        }
    }

    public static void openLocation(String addrName, double latitude, double longtitude, Context context){
//        Bundle bundle = new Bundle();
//        bundle.putString("Addr", addrName);
//        bundle.putDouble("Latitude", latitude);
//        bundle.putDouble("Longtitude", longtitude);
//
//        Intent intent = new Intent(context, BaiduMapTestActivity.class);
//        intent.putExtras(bundle);
//        context.startActivity(intent);
    }

//    public static void loadUrl(Context context, String url) {
//        Intent intent = new Intent(context, WebViewNewsActivity.class);
//        intent.putExtra(URL, url);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }
//
//    public static void loadUrl(Context context, String url, String title) {
//        Intent intent = new Intent(context, WebViewNewsActivity.class);
//        intent.putExtra(URL, url);
//        intent.putExtra(TITLE, title);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
//    }

    public static void copyText(Context context, String text){
        if((text == null) || text.length() == 0){
            Toast.makeText(context, "复制内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        cm.setPrimaryClip(ClipData.newPlainText("copy", text));
        Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
    }

    public static void callNumber(Context context, View targetView, String phoneNumber){
        Uri uri = Uri.parse("tel:"+phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL,uri);
        context.startActivity(intent);
    }

    public static void sendSMS(Context context, String number){
        Uri uri = Uri.parse("smsto:"+number);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "");
        context.startActivity(intent);
    }

    /**
     * 启动第三方apk
     *
     * 如果已经启动apk，则直接将apk从后台调到前台运行（类似home键之后再点击apk图标启动），如果未启动apk，则重新启动
     */
    public static void launchAPK(Context context, String packageName) {
        Intent intent = getAppOpenIntentByPackageName(context, packageName);
        context.startActivity(intent);
    }

    public static Intent getAppOpenIntentByPackageName(Context context, String packageName) {
        String mainAct = null;
        PackageManager pkgMag = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_NEW_TASK);

        @SuppressLint("WrongConstant") List<ResolveInfo> list = pkgMag.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        for (int i = 0; i < list.size(); i++) {
            ResolveInfo info = list.get(i);
            if (info.activityInfo.packageName.equals(packageName)) {
                mainAct = info.activityInfo.name;
                break;
            }
        }
        if (TextUtils.isEmpty(mainAct)) {
            return null;
        }
        intent.setComponent(new ComponentName(packageName, mainAct));
        return intent;
    }

//    /**
//     * @param orderInfo 接口返回的订单信息
//     */
//    public static void callAlipay(final Activity activity, final String orderInfo, Handler handler) {
//
//        Runnable payRunnable = new Runnable() {
//
//            @Override
//            public void run() {
//                PayTask alipay = new PayTask(activity);
//                Map<String, String> result = alipay.payV2(orderInfo, true);
//                if(handler != null) {
//                    Message msg = new Message();
//                    msg.what = SDK_PAY_FLAG;
//                    msg.obj = result;
//                    handler.sendMessage(msg);
//                }
//            }
//        };
//
//        Thread payThread = new Thread(payRunnable);
//        payThread.start();
//    }

    public static void takePicture(Context context){
        // 打开拍照程序
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        context.startActivity(intent);
//        context.startActivityForResult(intent, 1);
    }

    public static void takeVideo(Context context){
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        context.startActivity(intent);
//        activity.startActivityForResult(intent, 1);
    }
}
