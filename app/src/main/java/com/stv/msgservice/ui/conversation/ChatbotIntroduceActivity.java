package com.stv.msgservice.ui.conversation;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.GlideCircleWithBorder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ChatbotIntroduceActivity extends AppCompatActivity implements View.OnClickListener{
    private static String CHATBOT_NUMBER = "chatbot_number";
    private static String CHATBOT_BACKGROUND_URL = "chatbot_background_url";
    private static String CHATBOT_NAME = "chatbot_name";
    private ImageView iv_back;
    private TextView tv_chatbotNumber;
    private TextView tv_introduce;
    private ImageView iv_goto;
    private String chatbotId;
    private String chatbotName;
    private TextView readConversation;
    private FrameLayout introduceLayout;
    private LinearLayout logoLayout;
    private LinearLayout contentLayout;
    private RelativeLayout optionLayout;
    private ImageView layoutBackground;
    private String backgroundUrl;
    private TextView tv_chatbotName;
    //    private ImageView chatbot_logo;
    private ImageView chatbot_logo;
    private UserInfoViewModel userInfoViewModel;
    private ConversationListViewModel mConversationListViewModel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_introduce_activity);

//        ImmersionBar.with(this)
//            .statusBarDarkFont(true)
//            .navigationBarDarkIcon(true)
//            .transparentBar()
//            .init();
//        ImmersionBar.with(this).statusBarDarkFont(true).init();
//        setStatusBar();
        setStatusBarTransparent(this);
        chatbotId = getIntent().getStringExtra(CHATBOT_NUMBER);
        initView();
        queryChatbotInfo();
    }

    private void queryChatbotInfo(){
        userInfoViewModel.getUserInfo(chatbotId).observe(this, userInfoEntity -> {
            if(userInfoEntity != null){
                Log.i("Junwang", "chatbot log "+userInfoEntity.getPortrait());
//                chatbot_logo.setImageURI(Uri.parse(userInfoEntity.getPortrait()));
                Glide.with(this).load(userInfoEntity.getPortrait())
                        .placeholder(R.mipmap.avatar_def)
                        .transform(new CenterCrop(),new GlideCircleWithBorder())
//                        .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(this, 4)))
                        .into(chatbot_logo);
                chatbotName = userInfoEntity.getName();
                tv_chatbotName.setText(chatbotName);
                tv_introduce.setText(userInfoEntity.getDescription());
            }
        });
    }

    private void initView() {
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                getApplication(), null);
        userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);
        mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        tv_chatbotNumber = (TextView) findViewById(R.id.chatbot_number);
        tv_chatbotName = (TextView)findViewById(R.id.chatbot_name);
        String temp = chatbotId.substring(4);
        int i = temp.indexOf("@");
        tv_chatbotNumber.setText(temp.substring(0, i));

        chatbot_logo = (ImageView)findViewById(R.id.busn_logo);

        tv_introduce = (TextView) findViewById(R.id.tv_introduce);
        backgroundUrl = getIntent().getStringExtra(CHATBOT_BACKGROUND_URL);

        iv_goto = (ImageView) findViewById(R.id.goto_icon);
        iv_goto.setOnClickListener(this);

        readConversation = (TextView) findViewById(R.id.read_conversation);
        readConversation.setOnClickListener(this);

        if (backgroundUrl != null) {
            introduceLayout = (FrameLayout) findViewById(R.id.introduce_layout);
            logoLayout = (LinearLayout) findViewById(R.id.logo_layout);
            contentLayout = (LinearLayout) findViewById(R.id.content_layout);
            optionLayout = (RelativeLayout) findViewById(R.id.option_layout);
            introduceLayout.setBackground(null);
            logoLayout.setBackground(null);
            contentLayout.setBackground(null);
            optionLayout.setBackground(null);
            layoutBackground = (ImageView) findViewById(R.id.layout_background);
//            layoutBackground.setMinimumHeight(getWindow().getDecorView().getHeight());

            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//            setStatusBarTransparent(this);

            Glide.with(this)
                    .load(backgroundUrl)
                    .into(layoutBackground);
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    static void setMIUIBarDark(Window window, String key, boolean dark) {
        if (window != null) {
            Class<? extends Window> clazz = window.getClass();
            try {
                int darkModeFlag;
                Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField(key);
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    //状态栏透明且黑色字体
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
                } else {
                    //清除黑色字体
                    extraFlagField.invoke(window, 0, darkModeFlag);
                }
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.iv_back:
                finish();
                break;
            case R.id.goto_icon:
            case R.id.read_conversation:
                mConversationListViewModel.getConversationByChatbotId(chatbotId).observe(ChatbotIntroduceActivity.this, conversationEntity -> {
                    if(conversationEntity != null) {
                        Intent intent = new Intent(this, ConversationActivity.class);
                        intent.putExtra("conversation", conversationEntity);
                        this.startActivity(intent);
                    }else{
                        Intent intent = new Intent(this, ConversationActivity.class);
                        intent.putExtra("chatbotId", chatbotId);
                        intent.putExtra("conversationTitle", chatbotName);
                        this.startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
    }

    private void setStatusBarTransparent(Activity activity){
//        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
//        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

    }

    public static void start(Context context, String chatbotNumber, String backgroundUrl) {
        Intent intent = new Intent(context, ChatbotIntroduceActivity.class);
        intent.putExtra(CHATBOT_NUMBER, chatbotNumber);
        intent.putExtra(CHATBOT_BACKGROUND_URL, backgroundUrl);
        context.startActivity(intent);
    }

    protected boolean useThemestatusBarColor = false;//是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useStatusBarColor = true;//是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置

    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.action_bar_background_color));
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
