package com.stv.msgservice.ui.channel.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.SantiWebChromeClient;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import androidx.lifecycle.ViewModelProvider;

public class ChannelWebViewActivity extends BaseNoToolbarActivity {
    public static final String URL = "url";
    public static final String TITLE= "title";
    public static final String MSGID = "msgId";
    public static final String CHATBOT_LOGO = "chatbot_logo";
    public static final String CHATBOT_NAME = "chatbot_name";
    public static final String CHATBOT_ID = "chatbot_id";
    public static final String CHATBOT_ISATTENTIONED = "chatbot_isAttentioned";

    private ImageView mIVCancel;
    private WebView mWebView;
    private TextView mTVTitle;
    private String mUrl;
    private String mTitle;
//    private ProgressBar mPbLoading;
    private String mMsgId;
    private TextView mToolbarTitle;
    private ImageView mPortraitImageView;
    private TextView mNameTextView;
    private TextView mAttentionTv;

    private String mChatbotLogo;
    private String mChatbotName;
    private String mChatbotId;
    private int mChatbotIsAttentioned;

    private UserInfoViewModel userInfoViewModel;


    @Override
    protected int contentLayout() {
        return R.layout.channel_webview_activity;
    }

    @Override
    protected void afterViews() {
        Intent intent = getIntent();
        mTitle = intent.getStringExtra(TITLE);
        mUrl = intent.getStringExtra(URL);
        mChatbotLogo = intent.getStringExtra(CHATBOT_LOGO);
        mChatbotName = intent.getStringExtra(CHATBOT_NAME);
        mChatbotId = intent.getStringExtra(CHATBOT_ID);
        mChatbotIsAttentioned = intent.getIntExtra(CHATBOT_ISATTENTIONED, 0);
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                getApplication(), null);
        userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);
        initView();
        if(mUrl != null){
            initWebViewSetting();
        }
    }

    /**
     * 打开第三方app。如果没安装则跳转到应用市场
     * @param url
     */
    private void startThirdpartyApp(String url)
    {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); // 注释1
            if (getPackageManager().resolveActivity(intent, 0) == null)
            {  // 如果手机还没安装app，则跳转到应用市场
//                intent = new Intent(Intent.ACTION_VIEW, Uri.parse
//                        ("market://details?id=" + intent.getPackage())); // 注释2
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(ChannelWebViewActivity.this, "没有找到应用可以打开，请到应用市场下载！", Toast.LENGTH_LONG);
                    }
                });
                mWebView.stopLoading();
            }
            startActivity(intent);
        }
        catch (Exception e)
        {
            LogUtil.e("Junwang", e.getMessage());
        }
    }

    private void initView(){
        mToolbarTitle = (TextView)findViewById(R.id.toolbar_title);
        if(mTitle != null && mTitle.length() > 0){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            mToolbarTitle.setText(mTitle);
        }
//        mPbLoading = (ProgressBar)findViewById(R.id.pb_loading);
        mPortraitImageView = (ImageView)findViewById(R.id.portraitImageView);
        Glide.with(mPortraitImageView).load(mChatbotLogo).apply(new RequestOptions().placeholder(R.mipmap.avatar_def)).transform(new CenterCrop(),new GlideCircleWithBorder()).into(mPortraitImageView);
        mNameTextView = (TextView)findViewById(R.id.nameTextView);
        mNameTextView.setText(mChatbotName);
        mIVCancel = (ImageView)findViewById(R.id.iv_cancel);
        mIVCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mAttentionTv = (TextView)findViewById(R.id.attention_tv);
        if(mChatbotIsAttentioned != 0){
            mAttentionTv.setText("已关注");
            mAttentionTv.setTextColor(Color.BLACK);
            mAttentionTv.setBackgroundResource(R.drawable.channel_attentioned_textview_style);
        }else{
            mAttentionTv.setText("关注");
            mAttentionTv.setTextColor(Color.WHITE);
            mAttentionTv.setBackgroundResource(R.drawable.channel_attention_textview_style);
        }
        mAttentionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChatbotIsAttentioned == 0){
                    mChatbotIsAttentioned = 1;
                    mAttentionTv.setText("已关注");
                    mAttentionTv.setTextColor(Color.BLACK);
                    mAttentionTv.setBackgroundResource(R.drawable.channel_attentioned_textview_style);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            userInfoViewModel.updateAttentionByChatbotId(mChatbotId, mChatbotIsAttentioned);
                        }
                    }).start();
                }
            }
        });
        mWebView = (WebView)findViewById(R.id.wv_content);
        mWebView.setWebChromeClient(new SantiWebChromeClient(this, this));
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                mPbLoading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
//                mPbLoading.setVisibility(View.GONE);
                super.onPageFinished(view, url);
                String webTitle = view.getTitle();
                if (!TextUtils.isEmpty(webTitle)) {
                    if (TextUtils.isEmpty(mTitle) || !TextUtils.equals(webTitle, "about:blank")) {
                        setTitle(webTitle);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
//                mWebView.loadDataWithBaseURL(null, "升级维护中", "text/html", "utf-8", null);
                mWebView.setVisibility(View.INVISIBLE);
                ((TextView)findViewById(R.id.tv1)).setVisibility(View.VISIBLE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(request == null){
                    return false;
                }
                String url = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    url = request.getUrl().toString();
                } else {
                    url = request.toString();
                }

                if(url == null){
                    return true;
                }
                LogUtil.d("Junwang", "loadUrl url="+url);
                if(url.startsWith("weixin://wap/pay?") || url.startsWith("https://wx.tenpay.com")){
                    WXPay(view, url);
                    return true;
                }
                try{
                    if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")){
                        LogUtil.d("Junwang", "loadUrl url1="+url);

                        final PayTask task = new PayTask(ChannelWebViewActivity.this);
                        boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                            @Override
                            public void onPayResult(final H5PayResultModel result) {
                                final String url=result.getReturnUrl();
                                if(!TextUtils.isEmpty(url)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mWebView.loadUrl(url);
                                        }
                                    });
                                }
                            }
                        });

                        LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
                        if(!isIntercepted)
                            mWebView.loadUrl(url);
                        //return true;
                        return false;
                    }else {
                        startThirdpartyApp(url);
                        return true;
                    }
                }catch(Exception e){
                    return true;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                LogUtil.d("Junwang", "shouldOverrideUrlLoading loadUrl url.");
                if(url == null){
                    return true;
                }
                LogUtil.d("Junwang", "loadUrl url="+url);
                final PayTask task = new PayTask(ChannelWebViewActivity.this);
                boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                    @Override
                    public void onPayResult(final H5PayResultModel result) {
                        final String url=result.getReturnUrl();
                        if(!TextUtils.isEmpty(url)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mWebView.loadUrl(url);
                                }
                            });
                        }
                    }
                });

                LogUtil.d("Junwang", "isIntercepted = "+isIntercepted);
                if(!isIntercepted) {
                    if (url.startsWith("weixin://wap/pay?") || url.startsWith("http://weixin/wap/pay") ) {
                        try {
                            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(url)));
                            return true;
                        } catch (Exception e) {
                            LogUtil.i("Junwang", "weixin pay exception "+e.toString());
                        }
                    } else {
                        Map<String, String> extraHeaders = new HashMap<String, String>();
                        extraHeaders.put("Referer", "http://testxhs.supermms.cn");
                        if (url.startsWith("https://mclient.alipay.com") || url.startsWith("https://mclient.alipay.com")/*url.startsWith("alipays:") || url.startsWith("alipay")*/) {
                            mWebView.loadUrl(url);
                            return false;
                        } else {
                            if(url.startsWith("http://") || url.startsWith("https://") || url.startsWith("rtsp://")) {
                                view.loadUrl(url, extraHeaders);
                            }else{
                                startThirdpartyApp(url);
                                return true;
                            }
                        }
                    }
                    // ------- 处理结束 -------
                    if (!(url.startsWith("http") || url.startsWith("https"))) {
                        return true;
                    }
                }else{
                    startThirdpartyApp(url);
                    return true;
                }

                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
//                mPbLoading.setProgress(newProgress);
            }
        });
    }

    private static String getStringFromUrl(String s) throws IOException {
        StringBuffer buffer = new StringBuffer();
        // 通过js的执行路径获取后台数据进行解析
        java.net.URL url = new URL(s);
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setDoOutput(true);
        http.setDoInput(true);
        http.setUseCaches(false);
        http.setRequestMethod("GET");
        http.connect();
        // 将返回的输入流转换成字符串
        InputStream inputStream = http.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String str = null;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
        }
        bufferedReader.close();
        inputStreamReader.close();
        // 释放资源
        inputStream.close();
        inputStream = null;
        http.disconnect();
        str = buffer.toString();
        int index = str.indexOf("(");
        String jsonString = str.substring(index + 1, str.length() -1);
        return jsonString;
    }

    private boolean WXPay(WebView view, String url){
        //IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/"wxb4ba3c02aa476ea1");

        try{
            String content = getStringFromUrl(url);
            if(content != null && content.length() > 0){
                //Log.e("get server pay params:",content);
                JSONObject json = new JSONObject(content);
                if(null != json && !json.has("retcode") ){
                    PayReq req = new PayReq();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId			= json.getString("appid");
                    req.partnerId		= json.getString("partnerid");
                    req.prepayId		= json.getString("prepayid");
                    req.nonceStr		= json.getString("noncestr");
                    req.timeStamp		= json.getString("timestamp");
                    req.packageValue	= json.getString("package");
                    req.sign			= json.getString("sign");
                    req.extData			= "app data"; // optional
                    IWXAPI api = WXAPIFactory.createWXAPI(view.getContext(), /*"你的appid"*/req.appId);
                    //api.registerApp();
                    api.sendReq(req);
                    return true;
                }else{
                    LogUtil.d("Junwang", "返回错误"+json.getString("retmsg"));
                    Toast.makeText(this, "返回错误"+json.getString("retmsg"), Toast.LENGTH_SHORT).show();
                }
            }else{
                LogUtil.d("Junwang", "服务器请求错误");
                Toast.makeText(this, "服务器请求错误", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            LogUtil.d("Junwang", "异常："+e.getMessage());
            Toast.makeText(this, "异常："+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static void start(Context context, String url) {
        Intent intent = new Intent(context, ChannelWebViewActivity.class);
        LogUtil.i("Junwang", "ChannelWebViewActivity url="+url);
        intent.putExtra(URL, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void start(Context context, String url, String chatbotLogo, String chatbotName, String chatbotId, int mChatbotIsAttentioned){
        Intent intent = new Intent(context, ChannelWebViewActivity.class);
        LogUtil.i("Junwang", "ChannelWebViewActivity url="+url);
        intent.putExtra(URL, url);
        intent.putExtra(CHATBOT_LOGO, chatbotLogo);
        intent.putExtra(CHATBOT_NAME, chatbotName);
        intent.putExtra(CHATBOT_ID, chatbotId);
        intent.putExtra(CHATBOT_ISATTENTIONED, mChatbotIsAttentioned);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebViewSetting() {
        mWebView.setVerticalScrollBarEnabled(false);
        mWebView.setHorizontalScrollBarEnabled(false);
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
//        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(false);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setCacheMode(webSetting.LOAD_NO_CACHE);
        webSetting.setAppCachePath(getDir("appCache", Context.MODE_PRIVATE).getPath());
//        webSetting.setDatabasePath(getDir("databases", Context.MODE_PRIVATE).getPath());
//        webSetting.setGeolocationDatabasePath(getDir(/*"geolocation"*/"database", Context.MODE_PRIVATE).getPath());
        webSetting.setGeolocationDatabasePath(getFilesDir().getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setTextSize(WebSettings.TextSize.NORMAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setupWebView();
        mWebView.loadUrl(mUrl);
    }

    private void setupWebView() {
        mWebView.addJavascriptInterface(new ChannelWebViewActivity.JsInterfaceLogic(), "app");
    }

    @Override
    protected void onDestroy() {
        mWebView.stopLoading();
        mWebView.destroy();
        super.onDestroy();
    }

    /**
     *  暴露出去给JS调用的Java对象
     */
    class JsInterfaceLogic {
        private String phoneNumber;
        @JavascriptInterface
        public String getUserAccount() {
            String phoneNumber = null;//ChatbotUtils.getPhoneNumber();
            return phoneNumber == null ? /*"+8613777496301"*/"+8615735796495" : phoneNumber;
        }

        public String getPhoneNumber(){
            TelephonyManager phoneManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//        try{
//            phoneNumber = phoneManager.getLine1Number();
//        }catch(SecurityException e){
//            LogUtil.i("Junwang", "No permission to get phone number");
//        }finally {
//
//        }
            if(mMsgId != null) {
//                phoneNumber = SendRcsMsgUtils.getSelfNumber(mMsgId);
            }
            if(phoneNumber == null || phoneNumber.length() == 0){
                phoneNumber = /*"+8613777496301"*/"+8615735796495";
            }
            return phoneNumber;
        }
    }
}
