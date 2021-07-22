package com.stv.msgservice.ui.conversation.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuEntity;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuItem;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;
import com.stv.msgservice.utils.NativeFunctionUtil;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class ThreeButtonPopupMenuView extends LinearLayout {
    private OptionMenuView mMenuView;
    private PopLayout mPopLayout;
    private ImageView mSwitchBt;
    private TextView mMenuButton1;
    private TextView mMenuButton2;
    private TextView mMenuButton3;
    private ArrayList<String> mMenuItem1;
    private ArrayList<String> mMenuItem2;
    private ArrayList<String> mMenuItem3;
    private ArrayList<ButtonMenu.BusnMenuItem> mBusnMenuItem[];
    private String mButtonMenuAction[];
    private float mOffsetX;
    private int mOffsetY;
    private CustomSelectDialog mDialog1;
    private CustomSelectDialog mDialog2;
    private CustomSelectDialog mDialog3;
    private CustomSelectDialog mPopupDialog;
    private Context mContext;
    private FragmentActivity mActivity;

    public ThreeButtonPopupMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    private void onMenuItemClick(ButtonMenu.BusnMenuItem bmi, View targetView){
        if(bmi != null){
            int actionType = bmi.getAction_type();
            String actionUrl = bmi.getAction_url();
            int actionLocalFunc = bmi.getAction_native_function();
            switch (actionType){
                case 1:
                    //load url
                    Log.i("Junwang", "action type == 1"+", menu item name="+bmi.getItem_name());
                    NativeFunctionUtil.loadUrl(getContext(), actionUrl, bmi.getItem_name());
                    break;
                case 2:
                    //call native function
                    Log.i("Junwang", "action type == 2");
//                    if(actionLocalFunc == 1){
//                        NativeFunctionUtil.copyText(getContext(), "复制");
//                    }else if(actionLocalFunc == 2){
//                        NativeFunctionUtil.callNumber(ConversationMessageView.getActivityFromView(this), targetView, "10086");
//                    }
                    NativeFunctionUtil.callNativeFunction(actionLocalFunc, getContext(), null, targetView, bmi.getAction_url());
                    break;
                case 3:
                    //jump to app
                    Log.i("Junwang", "action type == 3");
                    NativeFunctionUtil.launchAPK(getContext(), actionUrl);
                    break;
                case 4:
                    //call alipay
                    Log.i("Junwang", "action type == 4");
//                    NativeFunctionUtil.callAlipay(ConversationMessageView.getActivityFromView(this), actionUrl, null);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        Log.i("Junwang", "PopupMenuView onFinishInflate");
        mSwitchBt = (ImageView)findViewById(R.id.switch_to_composemsg);
        mSwitchBt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ThreeButtonPopupMenuView.this.setVisibility(View.GONE);
            }
        });
        mMenuButton1 = (TextView)findViewById(R.id.menu_button1);

//        ViewGroup.LayoutParams ly = mMenuButton1.getLayoutParams();
//        mMenuButton1.getLeft();
//        mMenuButton1.getRight();
//        mPopLayout = (PopLayout) findViewById(R.id.pl_pop);
//        mMenuView = (OptionMenuView) findViewById(R.id.omv_menu);
        /*mMenuButton1*/((LinearLayout)findViewById(R.id.menu_button1_layout)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = -0.2143f; //-3/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();
//                final ArrayList<String> menuItem = new ArrayList<String>(){};
//                menuItem.add("拍照");
//                menuItem.add("相册");
//                menuItem.add("其他");
//                menuItem.add("查看历史记录");
//                menuItem.add("看看");
//                showCustomDialog(menuItem);
                if(mButtonMenuAction[0] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[0], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[0] != null) && (mBusnMenuItem[0].size() != 0)) {
                    if(mDialog1 != null && mDialog1.isShowing()){
                        mDialog1.dismiss();
                    }else {
//                        if(mPopupDialog != null && mPopupDialog.isShowing()){
//                            mPopupDialog.dismiss();
//                        }
                        mDialog1 = showCustomDialog(mMenuItem1, 0);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button1 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = -0.2143f; //-3/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();
                if(mButtonMenuAction[0] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[0], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[0] != null) && (mBusnMenuItem[0].size() != 0)) {
                    if(mDialog1 != null && mDialog1.isShowing()){
                        mDialog1.dismiss();
                    }else {
//                        if(mPopupDialog != null && mPopupDialog.isShowing()){
//                            mPopupDialog.dismiss();
//                        }
                        mDialog1 = showCustomDialog(mMenuItem1, 0);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button1 menu action is null and menuitem is null");
                }
            }
        });
        ((LinearLayout)findViewById(R.id.menu_button2_layout)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = 0.0714f; // 1/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();

                if(mButtonMenuAction[1] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[1], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[1] != null) && (mBusnMenuItem[1].size() != 0)) {
                    if(mDialog2 != null && mDialog2.isShowing()){
                        mDialog2.dismiss();
                    }else {
//                        if(mPopupDialog != null && mPopupDialog.isShowing()){
//                            mPopupDialog.dismiss();
//                        }
                        mDialog2 = showCustomDialog(mMenuItem2, 1);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button2 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton2 = (TextView)findViewById(R.id.menu_button2);
        mMenuButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = 0.0714f; // 1/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();

                if(mButtonMenuAction[1] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[1], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[1] != null) && (mBusnMenuItem[1].size() != 0)) {
                    if(mDialog2 != null && mDialog2.isShowing()){
                        mDialog2.dismiss();
                    }else {
//                        if(mPopupDialog != null && mPopupDialog.isShowing()){
//                            mPopupDialog.dismiss();
//                        }
                        mDialog2 = showCustomDialog(mMenuItem2, 1);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button2 menu action is null and menuitem is null");
                }
            }
        });
        ((LinearLayout)findViewById(R.id.menu_button3_layout)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = 0.357f; // 5/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();

                if(mButtonMenuAction[2] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[2], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[2] != null) && (mBusnMenuItem[2].size() != 0)) {
                    if(mDialog3 != null && mDialog3.isShowing()){
                        mDialog3.dismiss();
                    }else {
//                        if(mPopupDialog != null && mPopupDialog.isShowing()){
//                            mPopupDialog.dismiss();
//                        }
                        mDialog3 = showCustomDialog(mMenuItem3, 2);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button3 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton3 = (TextView)findViewById(R.id.menu_button3);
        mMenuButton3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("Junwang", "PopupMenuView onClick");
                mOffsetX = 0.357f; // 5/14
                mOffsetY = ThreeButtonPopupMenuView.this.getHeight();

                if(mButtonMenuAction[2] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[2], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[2] != null) && (mBusnMenuItem[2].size() != 0)) {
                    if(mDialog3 != null && mDialog3.isShowing()){
                        mDialog3.dismiss();
                    }else {
                        if(mPopupDialog != null && mPopupDialog.isShowing()){
                            mPopupDialog.dismiss();
                        }
                        mDialog3 = showCustomDialog(mMenuItem3, 2);
                    }
                }else{
                    if(mPopupDialog != null && mPopupDialog.isShowing()){
                        mPopupDialog.dismiss();
                    }
                    Log.i("Junwang", "error! button3 menu action is null and menuitem is null");
                }
            }
        });
        super.onFinishInflate();
    }

    public void setMenu(FragmentActivity activity, ChatbotMenuEntity menuEntity){
        mActivity = activity;
        ChatbotMenuItem[] menuItem = menuEntity.getMenu().getEntries();
        if(menuItem == null){
            Log.i("Junwang", "menuItem is null");
            return;
        }
        mMenuItem1 = new ArrayList<String>(){};
        mMenuItem2 = new ArrayList<String>(){};
        mMenuItem3 = new ArrayList<String>(){};
        mBusnMenuItem = new ArrayList[3];
        mButtonMenuAction = new String[3];
        int i=0;
        Drawable left = getResources().getDrawable(R.mipmap.icon_accordion);
        left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        for(ChatbotMenuItem menuitem : menuItem){
            i++;
            if(i == 1){
                if(menuitem.getMenu() != null){
//                    Log.i("Junwang", "22222 name="+menuitem.getMenu().getDisplayText());
                    mMenuButton1.setText(menuitem.getMenu().getDisplayText());
                    SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                    if(childMenu != null){
                        mBusnMenuItem[0] = new ArrayList<>();
                        for(SuggestionActionWrapper child : childMenu){
                            if(child != null && child.getReply() != null){
                                Log.i("Junwang", "name="+child.getReply().getDisplayText());
                                ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                                mBusnMenuItem[0].add(item);
                                mMenuItem1.add(child.getReply().displayText);
                            }
//                        mButtonMenuAction[0] = child.getReply().postback.getData();
                        }
                        if(mMenuItem1.size() > 1){
                            mMenuButton1.setCompoundDrawables(left, null, null, null);
                        }
                    }
                }
            }else if(i == 2){
                if(menuitem.getMenu() != null){
                    mMenuButton2.setText(menuitem.getMenu().getDisplayText());
                    SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                    mBusnMenuItem[1] = new ArrayList<>();
                    if(childMenu != null){
                        for(SuggestionActionWrapper child : childMenu){
                            if(child != null && child.getReply() != null){
                                ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                                mBusnMenuItem[1].add(item);
                                mMenuItem2.add(child.getReply().displayText);
//                        mButtonMenuAction[1] = child.getReply().postback.getData();
                            }
                        }
                        if(mMenuItem2.size() > 1){
                            mMenuButton2.setCompoundDrawables(left, null, null, null);
                        }
                    }
                }
            }else if(i==3){
                if(menuitem.getMenu() != null){
                    mMenuButton3.setText(menuitem.getMenu().getDisplayText());
                    SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                    mBusnMenuItem[2] = new ArrayList<>();
                    if(childMenu != null){
                        for(SuggestionActionWrapper child : childMenu){
                            if(child != null && child.getReply() != null){
                                ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                                mBusnMenuItem[2].add(item);
                                mMenuItem3.add(child.getReply().displayText);
//                        mButtonMenuAction[2] = child.getReply().postback.getData();
                            }
                        }
                        if(mMenuItem3.size() > 1){
                            mMenuButton3.setCompoundDrawables(left, null, null, null);
                        }
                    }
                }
            }
        }
    }


    /**
     * 展示对话框视图，构造方法创建对象
     */
    private CustomSelectDialog showDialog(CustomSelectDialog.SelectDialogListener listener, List<String> names) {
        mPopupDialog = new CustomSelectDialog(mActivity,
                R.style.transparentFrameWindowStyle, listener, names);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = dm.widthPixels;         // 屏幕宽度（像素）
        float density = dm.density;         // 屏幕密度（0.75 / 1.0 / 1.5）
        // 屏幕宽度算法:屏幕宽度（像素）/屏幕密度
        int screenWidth = (int) (width / density);  // 屏幕宽度(dp)
        Log.i("Junwang", "width="+width+", density="+density+", screenWidth="+screenWidth+", ll="+(int)(screenWidth/3.5));
        mPopupDialog.setPopupMenuLayoutParams(mOffsetX, /*mMenuButton1.getWidth()*/(int)(width/3.1), mOffsetY);
        Log.i("Junwang", "CustomSelectDialog left="+mMenuButton1.getLeft()+", x="+ mMenuButton1.getX()+", right="
                +mMenuButton1.getRight()+", top="+mMenuButton1.getTop()+", height="+mMenuButton1.getY()+", parent_height="+this.getHeight());
        mPopupDialog.setItemColor(R.color.colorAccent,R.color.colorPrimary);
        Window window = mPopupDialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //判断activity是否finish
        if (/*!this.isFinishing()*/true) {
            mPopupDialog.show();
        }

        return mPopupDialog;
    }

    private CustomSelectDialog showCustomDialog(ArrayList<String> menuItem, int buttonNo) {
        final List<String> names = new ArrayList<>(menuItem);

        if(mPopupDialog != null && mPopupDialog.isShowing()){
            mPopupDialog.dismiss();
        }
        return showDialog(new CustomSelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Junwang", "showCustomDialog onItemClick position="+position);
                onMenuItemClick(mBusnMenuItem[buttonNo].get(position), view);
//                Toast.makeText(getContext(), /*names.get(position)*/mBusnMenuItem[buttonNo].get(position).getAction_url(), Toast.LENGTH_SHORT).show();
            }
        }, names);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("Junwang", "Three Button Menu onTouchEvent");
//        return super.onTouchEvent(event);
//    }

    public void closeMenu(){
        if(mPopupDialog != null && mPopupDialog.isShowing()){
            mPopupDialog.dismiss();
        }
    }
}