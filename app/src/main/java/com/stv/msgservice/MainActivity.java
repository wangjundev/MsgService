package com.stv.msgservice;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Telephony;
import android.widget.TextView;

import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.receiver.MmsWapPushReceiver;
import com.stv.msgservice.receiver.SmsReceiver;
import com.stv.msgservice.ui.WfcBaseActivity;
import com.stv.msgservice.ui.conversationlist.ConversationListFragment;
import com.stv.msgservice.utils.PermissionUtils;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;

public class MainActivity extends WfcBaseActivity {
    MmsWapPushReceiver mmsWapPushReceiver;
    private AppExecutors mAppExecutors;
    @BindView(R2.id.toolbar_title)
    TextView toolbarTitle;

    private final String[] BASIC_PERMISSIONS = new String[]{
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
        toolbarTitle.setText("msgservice");
        final PackageManager packageManager = this.getPackageManager();
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, SmsReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        packageManager.setComponentEnabledSetting(
                new ComponentName(this, MmsWapPushReceiver.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
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
    }

    //for test simulate
    public void simulateReceivedMsg(){
        final String from = /*"10086"*/"1065805710000";
        mAppExecutors.diskIO().execute(() -> {
            final String text = "http://172.16.0.96:1995/";
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            mViewModel.saveMsg(this, text, from, true,  null,1);

        });
    }

    public void saveMsg(Context context, String content, String destination, boolean isReceived, String attachmentpath, int messageType){
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            mViewModel.saveMsg(context, content, destination, isReceived,  attachmentpath,messageType);

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