package com.stv.msgservice.utils;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.third.activity.ShowLocationActivity;
import com.stv.msgservice.third.utils.OpenLocalMapUtil;
import com.stv.msgservice.ui.WebViewNewsActivity;

import java.util.List;

import androidx.appcompat.app.AlertDialog;

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
     * ????????????????????????
     * @param gd_lon
     * @param gd_lat
     * @return
     */
    private static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }


    /**
     * ??????????????????????????????(?????????????????????????????????????????????)
     * @param context
     * @param slat ????????????
     * @param slon ????????????
     * @param sname ???????????? ????????????0,0???null???
     * @param dlat ????????????
     * @param dlon ????????????
     * @param dname ???????????? ??????
     */
    public static void openBaiDuNavi(Context context,double slat, double slon, String sname, double dlat, double dlon, String dname){
        String uriString = null;
        //??????????????????
//        ??????????????????????????????BaiduLBS_Android.jar???
//        LatLng destination = new LatLng(dlat,dlon);
//        LatLng destinationLatLng = GCJ02ToBD09(destination);
//        dlat = destinationLatLng.latitude;
//        dlon = destinationLatLng.longitude;

        double destination[] = gaoDeToBaidu(dlat, dlon);
        dlat = destination[0];
        dlon = destination[1];

        StringBuilder builder = new StringBuilder("baidumap://map/direction?mode=driving&");
        if (slat != 0){
            //??????????????????

//            LatLng origin = new LatLng(slat,slon);
//            LatLng originLatLng = GCJ02ToBD09(origin);
//            slat = originLatLng.latitude;
//            slon = originLatLng.longitude;

            double[] origin = gaoDeToBaidu(slat, slon);
            slat = origin[0];
            slon = origin[1];

            builder.append("origin=latlng:")
                    .append(slat)
                    .append(",")
                    .append(slon)
                    .append("|name:")
                    .append(sname);
        }
        builder.append("&destination=latlng:")
                .append(dlat)
                .append(",")
                .append(dlon)
                .append("|name:")
                .append(dname);
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.baidu.BaiduMap");
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }


    /**
     * ??????????????????
     */
    /**
     *
     * @param slat ??????
     * @param slon ??????
     * @param content ??????
     */
    private static void openBaiduMap(double slat, double slon, String content, Context context, boolean mIsMapOpened) {
        if (OpenLocalMapUtil.isBaiduMapInstalled()) {
            try {
                String uri = OpenLocalMapUtil.getBaiduMapUri(String.valueOf(slat), String.valueOf(slon), content);
                Intent intent = new Intent();
                intent.setData(Uri.parse(uri));
                context.startActivity(intent); //????????????
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
     * ??????????????????
     */
    /**
     *
     * @param dlat ??????
     * @param dlon ??????
     * @param content ??????
     */
    private static void openGaoDeMap(double dlat, double dlon, String content, Context context, boolean mIsMapOpened) {
        if (OpenLocalMapUtil.isGdMapInstalled()) {
            try {
                //????????????????????????????????????????????????????????????
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
                context.startActivity(intent); //????????????
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
     * ???????????????????????????????????????
     */
    /**
     *
     * @param dlat ??????
     * @param dlon ??????
     * @param dname ??????
     * @param content ????????????
     */
    private static void openWebMap(double dlat, double dlon, String dname, String content, Context context) {
        Uri mapUri = Uri.parse(OpenLocalMapUtil.getWebBaiduMapUri(
                String.valueOf(dlat), String.valueOf(dlon),
                dname, content, /*APP_NAME*/"com.stv.msgservice"));
        Intent loction = new Intent(Intent.ACTION_VIEW, mapUri);
        context.startActivity(loction);
    }

    public static void open3rdMapNavigation(String addrName, double latitude, double longtitude, Context context){
        boolean mIsBaiduMapInstalled = OpenLocalMapUtil.isBaiduMapInstalled();
        boolean mIsGaodeMapInstalled = OpenLocalMapUtil.isGdMapInstalled();
        boolean mIsMapOpened = false;
        if(mIsBaiduMapInstalled && mIsGaodeMapInstalled){
            showSelectMapDialog(addrName, latitude, longtitude, context);
        }else if(mIsBaiduMapInstalled){
            openBaiduMap(latitude,longtitude, addrName, context, mIsMapOpened);
        }else if(mIsGaodeMapInstalled)
        {
            openGaoDeMap(latitude,longtitude, addrName, context, mIsMapOpened);
        }else{
            openWebMap(latitude,longtitude, "??????", addrName, context);
        }
    }

    public static void showSelectMapDialog(String addrName, double latitude, double longtitude, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("?????????");
        builder.setItems(new CharSequence[]{"????????????", "????????????"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    openBaiDuNavi(context, 0, 0, null, latitude, longtitude, addrName);
//                    openBaiduMap(latitude,longtitude, addrName, context, false);
                }else{
                    openGaoDeMap(latitude,longtitude, addrName, context, false);
                }
            }
        });
        builder.show();
    }

    public static void openLocation(String addrName, double latitude, double longtitude, Context context){
        Intent intent = new Intent(context, ShowLocationActivity.class);
        intent.putExtra("Lat", latitude);
        intent.putExtra("Long", longtitude);
        intent.putExtra("title", addrName);
        context.startActivity(intent);
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
            Toast.makeText(context, "????????????????????????", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager cm = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        assert cm != null;
        cm.setPrimaryClip(ClipData.newPlainText("copy", text));
        Toast.makeText(context, "?????????", Toast.LENGTH_SHORT).show();
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
     * ???????????????apk
     *
     * ??????????????????apk???????????????apk????????????????????????????????????home??????????????????apk?????????????????????????????????apk??????????????????
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
//     * @param orderInfo ???????????????????????????
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
        // ??????????????????
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
