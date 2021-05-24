package com.stv.msgservice.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.stv.msgservice.third.utils.OpenLocalMapUtil;
import com.stv.msgservice.ui.WebViewNewsActivity;

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

    /**
     * 打开百度地图
     */
    /**
     *
     * @param slat 纬度
     * @param slon 经度
     * @param content 内容
     */
    private static void openBaiduMap(double slat, double slon, String content, Context context, boolean mIsMapOpened) {
        if (OpenLocalMapUtil.isBaiduMapInstalled()) {
            try {
                String uri = OpenLocalMapUtil.getBaiduMapUri(String.valueOf(slat), String.valueOf(slon), content);
                Intent intent = new Intent();
                intent.setData(Uri.parse(uri));
                context.startActivity(intent); //启动调用
                mIsMapOpened = true;
            } catch (Exception e) {
                mIsMapOpened = false;
                e.printStackTrace();
            }
        } else {
            mIsMapOpened = false;
        }
    }

    /**
     * 打开高德地图
     */
    /**
     *
     * @param dlat 纬度
     * @param dlon 纬度
     * @param content 终点
     */
    private static void openGaoDeMap(double dlat, double dlon, String content, Context context, boolean mIsMapOpened) {
        if (OpenLocalMapUtil.isGdMapInstalled()) {
            try {
                //百度地图定位坐标转换成高德地图可识别坐标
                double[] loca = new double[2];
                //loca = OpenLocalMapUtil.gcj02_To_Bd09(dlat, dlon);
                loca = OpenLocalMapUtil.bd09_To_Gcj02(dlat, dlon);
                String uri = OpenLocalMapUtil.getGdMapUri(/*APP_NAME*/"com.stv.msgservice",
                        String.valueOf(loca[0]), String.valueOf(loca[1]), content);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setPackage("com.autonavi.minimap");
                intent.setData(Uri.parse(uri));
                context.startActivity(intent); //启动调用
                mIsMapOpened = true;

            } catch (Exception e) {
                mIsMapOpened = false;
                e.printStackTrace();
            }
        } else {
            mIsMapOpened = false;
        }
    }

    /**
     * 打开浏览器进行百度地图导航
     */
    /**
     *
     * @param dlat 纬度
     * @param dlon 经度
     * @param dname 终点
     * @param content 地点内容
     */
    private static void openWebMap(double dlat, double dlon, String dname, String content, Context context) {
        Uri mapUri = Uri.parse(OpenLocalMapUtil.getWebBaiduMapUri(
                String.valueOf(dlat), String.valueOf(dlon),
                dname, content, /*APP_NAME*/"com.stv.msgservice"));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        context.startActivity(loction);
    }

    public static void openLocation(String addrName, double latitude, double longtitude, Context context){
        boolean mIsBaiduMapInstalled = OpenLocalMapUtil.isBaiduMapInstalled();
        boolean mIsGaodeMapInstalled = OpenLocalMapUtil.isGdMapInstalled();
        boolean mIsMapOpened = false;
        if(mIsBaiduMapInstalled && mIsGaodeMapInstalled){
            try {
                Dialog dialog = new Dialog(context);
                dialog.setTitle("请选择");
                dialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(mIsBaiduMapInstalled){
            openBaiduMap(latitude,longtitude, addrName, context, mIsMapOpened);
        }else if(mIsGaodeMapInstalled)
        {
            openGaoDeMap(latitude,longtitude, addrName, context, mIsMapOpened);
        }else{
            openWebMap(latitude,longtitude, "终点", addrName, context);
        }
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
    public static void loadUrl(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewNewsActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

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
