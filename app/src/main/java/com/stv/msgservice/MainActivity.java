package com.stv.msgservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;

import com.stv.msgservice.core.sms.MmsUtils;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.network.ParsedMsgBean;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.receiver.MmsWapPushReceiver;
import com.stv.msgservice.receiver.SmsReceiver;
import com.stv.msgservice.ui.WfcBaseActivity;
import com.stv.msgservice.ui.conversationlist.ConversationListFragment;
import com.stv.msgservice.utils.PermissionUtils;

import java.util.Arrays;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;

import static com.stv.msgservice.core.sms.MmsUtils.parseMsgPdu;

public class MainActivity extends WfcBaseActivity {
    MmsWapPushReceiver mmsWapPushReceiver;
    SmsReceiver smsReceiver;
    private AppExecutors mAppExecutors;
    @BindView(R2.id.toolbar_title)
    TextView toolbarTitle;

    private final String[] BASIC_PERMISSIONS = new String[]{
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int PERMISSION_REQUEST_CODE = 100001;

    @Override
    protected int contentLayout() {
        return R.layout.main_activity;
    }

    @Override
    protected void afterViews() {
        getToolbar().setNavigationIcon(null);
//        super.afterViews();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, ConversationListFragment.newInstance())
                .commitNow();
        mAppExecutors = new AppExecutors();
//        RetrofitManager
//                .init(new MsgRetrofitBuilder());
        initPermission();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(/*"msgservice"*/"5G消息");
        final PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, SmsReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, MmsWapPushReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        registerReceiver();
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main_activity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, ConversationListFragment.newInstance())
//                    .commitNow();
//        }
//
//        mAppExecutors = new AppExecutors();
////        RetrofitManager
////                .init(new MsgRetrofitBuilder());
//        initPermission();
//        final PackageManager packageManager = this.getPackageManager();
//        packageManager.setComponentEnabledSetting(
//                new ComponentName(this, SmsReceiver.class),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//        packageManager.setComponentEnabledSetting(
//                new ComponentName(this, MmsWapPushReceiver.class),
//                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
//    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean has = PermissionUtils.checkPermissions(this, BASIC_PERMISSIONS);
            if (!has) {
                PermissionUtils.requestPermissions(this, PERMISSION_REQUEST_CODE,
                        BASIC_PERMISSIONS);
            }
        }
    }

    private void registerReceiver(){
        mmsWapPushReceiver = new MmsWapPushReceiver(this, mAppExecutors);
        IntentFilter filter = new IntentFilter(Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION);
        registerReceiver(mmsWapPushReceiver, filter);

        smsReceiver = new SmsReceiver(this, mAppExecutors);
        IntentFilter filter1 = new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        registerReceiver(smsReceiver, filter1);
    }

    private void unregisterReceiver(){
        if(mmsWapPushReceiver != null){
            unregisterReceiver(mmsWapPushReceiver);
        }
        if(smsReceiver != null){
            unregisterReceiver(smsReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    //for test simulate
    public void simulateReceivedMsg(){
        final String from = /*"10086"*/"1065805710000";
//        mAppExecutors.diskIO().execute(() -> {
//            final String text = "http://172.16.0.96:1995/";
//            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
//            mViewModel.getXml(this, text);
//        });
//        byte[] data = new byte[]{-116, -126, -124, 65, 66, 67, 124, 97, 98, 99, 0, -1};
//        simulateParsebody(this, data, null);

        byte[] pdu = new byte[]{-116, -126, -104, 49, 52, 48, 55, 57, 57, 51, 56, 56, 51, 57, 50, 56, 57, 53, 54, 57, 50, 56, 92, 124, 99, 97, 108, 108, 98, 97, 99, 107, 46, 115, 117, 112, 101, 114, 109, 109, 115, 46, 99, 110, 0, -115, -112};
        byte[] data = MmsUtils.processReceivedPdu(
                this, pdu, -1, "10086");
        Log.i("Junwang", "wap content="+new String(data));
        if(data == null || data.length < 1){
            return;
        }
        simulateParsebody(this, data, null);
    }


    public String simulateParsebody(final Context context, byte[] body, SmsMessage[] messages) {
        try {
            if (body != null && body.length > 0) {
//                final String from = parsedPdu.getFrom().getString();
                mAppExecutors.diskIO().execute(() -> {
                    Log.i("Junwang", "MmsWapPushReceiver parsed body = " + Arrays.toString(body));
                    ParsedMsgBean msgBean = new ParsedMsgBean();
                    parseMsgPdu(body, msgBean);
                    Log.i("Junwang", "MmsWapPushReceiver parsed orderNo = " + msgBean.getOrderNo()+", parsed domain="+msgBean.getDomain());
                    ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                    mViewModel.getXml(this, msgBean.getOrderNo(), msgBean.getDomain());
                });
            }

        } catch (Exception e) {
            Log.e("Junwang", "parsePdu error " + e.toString());
        }
        return null;
    }

    public void saveMsg(Context context, String content, String destination, boolean isReceived, String attachmentpath, String thumbnail, int messageType){
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            mViewModel.saveMsg(context, content, destination, isReceived,  attachmentpath, thumbnail, messageType);

        });
    }

    public void deleteConversation(ConversationEntity ce){
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                getApplication(), 0);
        MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);
        messageViewModel.getMessages(ce.getId()).observe(this, messageEntityList -> {
            mAppExecutors.diskIO().execute(() -> {
                ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                mViewModel.deleteConversation(ce);
                messageViewModel.deleteMessages(messageEntityList);
            });
        });
    }

    public AppExecutors getAppExecutors() {
        return mAppExecutors;
    }
}