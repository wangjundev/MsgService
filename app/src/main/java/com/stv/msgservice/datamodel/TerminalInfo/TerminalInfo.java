package com.stv.msgservice.datamodel.TerminalInfo;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class TerminalInfo {

    private static final Logger sLogger = Logger.getLogger(TerminalInfo.class.getName());

    /**
     * Product name
     */
    private static final String productName = "RCS-client";

    /**
     * Product version
     */
    private static String sProductVersion;

    /**
     * RCS client version. Client Version Value = Platform "-" VersionMajor "." VersionMinor
     * Platform = Alphanumeric (max 9) VersionMajor = Number (2 char max) VersionMinor = Number (2
     * char max)
     */
    private static final String CLIENT_VERSION_PREFIX = "RCSAndr-";

    private static final String UNKNOWN = "unknown";

    private static final char FORWARD_SLASH = '/';

    private static final char HYPHEN = '-';

    private static String sClientVersion;

    private static String sBuildInfo;

    private static String sClientInfo;

    /**
     * Returns the product name
     *
     * @return Name
     */
    public static String getProductName() {
        return productName;
    }


    /**
     * Returns the client version as mentioned under versionName in AndroidManifest, prefixed with
     * CLIENT_VERSION_PREFIX.
     * <p>
     * In case versionName is not found under AndroidManifest it will default to UNKNOWN.
     * </p>
     *
     * @param ctx the context
     * @return Client version
     */
    public static String getClientVersion(Context ctx) {
//        if (sClientVersion == null) {
//            sClientVersion = CLIENT_VERSION_PREFIX + getProductVersion(ctx);
//        }
        return sClientVersion;
    }

    /**
     * Returns the client vendor
     *
     * @return Build.MANUFACTURER
     */
    public static String getClientVendor() {
        return (Build.MANUFACTURER != null) ? Build.MANUFACTURER : UNKNOWN;
    }

    /**
     * Returns the terminal vendor
     *
     * @return Build.MANUFACTURER
     */
    public static String getTerminalVendor() {
        return (Build.MANUFACTURER != null) ? Build.MANUFACTURER : UNKNOWN;
    }

    /**
     * Returns the terminal model
     *
     * @return Build.DEVICE
     */
    public static String getTerminalModel() {
        return (Build.DEVICE != null) ? Build.DEVICE : UNKNOWN;
    }

    /**
     * Returns the terminal software version
     *
     * @return Build.DISPLAY
     */
    public static String getTerminalSoftwareVersion() {
        return (Build.DISPLAY != null) ? Build.DISPLAY : UNKNOWN;
    }

    /**
     * Get the build info
     *
     * @return build info
     */
    public static String getBuildInfo() {
        if (sBuildInfo == null) {
            final String buildVersion = getTerminalModel() + HYPHEN + getTerminalSoftwareVersion();
            sBuildInfo = getTerminalVendor() + FORWARD_SLASH + buildVersion;
        }
        return sBuildInfo;
    }

    /**
     * Returns the client_vendor '/' client_version
     *
     * @return client information
     */
    public static String getClientInfo() {
//        if (sClientInfo == null) {
//            sClientInfo = getClientVendor() + FORWARD_SLASH
//                    + getClientVersion(AndroidFactory.getApplicationContext());
//        }
        return sClientInfo;
    }

    /**
     * ??????????????????apk?????????
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //??????????????????????????????AndroidManifest.xml???android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * ?????????????????????
     *
     * @param context ?????????
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    public static String getMEID() {
        try {
            Class clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);

            String meid = (String) method.invoke(null, "ril.cdma.meid", "");
            if (!TextUtils.isEmpty(meid)) {
                Log.d("Junwang", "getMEID meid: " + meid);
                return meid;
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.w("Junwang", "getMEID error : " + e.getMessage());
        }
        return "";
    }

    public static String getMEID(Context context){
        //?????????TelephonyManager??????
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        Method method = null;
        try {
            method = telephonyManager.getClass().getMethod("getDeviceId", int.class);
            //??????IMEI???
//            @SuppressLint("MissingPermission")
//            String imei1 = telephonyManager.getDeviceId();
//            String imei2 = (String) method.invoke(telephonyManager, 1);
            //??????MEID???
            String meid = (String) method.invoke(telephonyManager, 2);
            return meid;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ???????????????AndroidId
     *
     * @param context ?????????
     * @return ?????????AndroidId
     */
    public static String getAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * ??????????????????????????????WTK7N16923005607???, ????????????????????????
     *
     * @return ???????????????
     */
    public static String getSERIAL() {
        try {
            return Build.SERIAL;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
}
