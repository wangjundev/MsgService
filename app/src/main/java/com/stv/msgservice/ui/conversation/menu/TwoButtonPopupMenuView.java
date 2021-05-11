package com.stv.msgservice.ui.conversation.menu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuEntity;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuItem;
import com.stv.msgservice.datamodel.network.chatbot.SuggestionActionWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class TwoButtonPopupMenuView extends LinearLayout {
    private OptionMenuView mMenuView;
    private PopLayout mPopLayout;
    private ImageView mSwitchBt;
    private TextView mMenuButton1;
    private TextView mMenuButton2;
    private ArrayList<String> mMenuItem1;
    private ArrayList<String> mMenuItem2;
    private ArrayList<ButtonMenu.BusnMenuItem> mBusnMenuItem[];
    private String mButtonMenuAction[];
    private float mOffsetX;
    private int mOffsetY;
    private CustomSelectDialog mDialog;
    private FragmentActivity mActivity;

    public TwoButtonPopupMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

//    private void onMenuItemClick(ButtonMenu.BusnMenuItem bmi, View targetView){
//        if(bmi != null){
//            int actionType = bmi.getAction_type();
//            String actionUrl = bmi.getAction_url();
//            int actionLocalFunc = bmi.getAction_native_function();
//            switch (actionType){
//                case 1:
//                    //load url
//                    Log.i("Junwang", "action type == 1");
//                    NativeFunctionUtil.loadUrl(getContext(), actionUrl);
//                    break;
//                case 2:
//                    //call native function
//                    Log.i("Junwang", "action type == 2");
//                    NativeFunctionUtil.callNativeFunction(actionLocalFunc, ConversationMessageView.getActivityFromView(this), null, targetView, bmi.getAction_url());
//                    break;
//                case 3:
//                    //jump to app
//                    Log.i("Junwang", "action type == 3");
//                    NativeFunctionUtil.launchAPK(getContext(), actionUrl);
//                    break;
//                case 4:
//                    //call alipay
//                    Log.i("Junwang", "action type == 4");
//                    NativeFunctionUtil.callAlipay(ConversationMessageView.getActivityFromView(this), actionUrl, null);
//                    break;
//                default:
//                    break;
//            }
//        }
//    }

    @Override
    protected void onFinishInflate() {
        Log.i("PopupMenuView", "PopupMenuView onFinishInflate");
        mSwitchBt = (ImageView)findViewById(R.id.switch_to_composemsg);
        mMenuButton1 = (TextView)findViewById(R.id.menu_button1);
        ((LinearLayout)findViewById(R.id.menu_button1_layout)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = -0.2143f; //-3/14
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();
                if(mButtonMenuAction[0] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[0], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[0] != null) && (mBusnMenuItem[0].size() != 0)) {
                    showCustomDialog(mMenuItem1, 0);
                }else{
                    Log.i("Junwang", "error! button1 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = -0.1f; //0.2+0.4/2-0.5
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();
                if(mButtonMenuAction[0] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[0], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[0] != null) && (mBusnMenuItem[0].size() != 0)) {
                    showCustomDialog(mMenuItem1, 0);
                }else{
                    Log.i("Junwang", "error! button1 menu action is null and menuitem is null");
                }
            }
        });
        ((LinearLayout)findViewById(R.id.menu_button2_layout)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = 0.0714f; // 1/14
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();

                if(mButtonMenuAction[1] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[1], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[1] != null) && (mBusnMenuItem[1].size() != 0)) {
                    showCustomDialog(mMenuItem2, 1);
                }else{
                    Log.i("Junwang", "error! button2 menu action is null and menuitem is null");
                }
            }
        });
        mMenuButton2 = (TextView)findViewById(R.id.menu_button2);
        mMenuButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.i("PopupMenuView", "PopupMenuView onClick");
                mOffsetX = 0.3f; // 1-0.4/2-0.5
                mOffsetY = TwoButtonPopupMenuView.this.getHeight();
//                final ArrayList<String> menuItem = new ArrayList<String>(){};
//                menuItem.add("打电话");
//                menuItem.add("发短信");
//                menuItem.add("新年快乐");
//                menuItem.add("我的");
//                showCustomDialog(menuItem);
                if(mButtonMenuAction[1] != null){
                    Toast.makeText(getContext(), /*names.get(position)*/mButtonMenuAction[1], Toast.LENGTH_SHORT).show();
                }else if((mBusnMenuItem[1] != null) && (mBusnMenuItem[1].size() != 0)) {
                    showCustomDialog(mMenuItem2, 1);
                }else{
                    Log.i("Junwang", "error! button2 menu action is null and menuitem is null");
                }
            }
        });
        super.onFinishInflate();
    }

    //add by junwang for chatbot menu
    public void setMenu(FragmentActivity activity, ChatbotMenuEntity menuEntity){
        mActivity = activity;
        ChatbotMenuItem[] menuItem = menuEntity.getMenu().getEntries();
        if(menuItem == null || menuItem.length == 0){
            return;
        }
        int i = 0;
        mMenuItem1 = new ArrayList<String>(){};
        mMenuItem2 = new ArrayList<String>(){};
        mBusnMenuItem = new ArrayList[2];
        mButtonMenuAction = new String[2];
        Drawable left = getResources().getDrawable(R.mipmap.icon_accordion);
        left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        for(ChatbotMenuItem menuitem : menuItem){
            i++;
            if(i == 1){
                mMenuButton1.setText(menuitem.getMenu().getDisplayText());
                mBusnMenuItem[0] = new ArrayList<>();
                SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                if(childMenu != null){
                    for(SuggestionActionWrapper child : childMenu){
                        mMenuItem1.add(child.getReply().displayText);
                        ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                        mBusnMenuItem[0].add(item);
                    }
                    if(mMenuItem1.size() > 1){
                        mMenuButton1.setCompoundDrawables(left, null, null, null);
                    }
                }
            }else if(i == 2){
                mMenuButton2.setText(menuitem.getMenu().getDisplayText());
                mBusnMenuItem[1] = new ArrayList<>();
                SuggestionActionWrapper[] childMenu = menuitem.getMenu().getEntries();
                if(childMenu != null){
                    for(SuggestionActionWrapper child : childMenu){
                        ButtonMenu.BusnMenuItem item = new ButtonMenu().new BusnMenuItem(child.getReply().getDisplayText(), child.getReply().getPostback().data, 1, 0);
                        mBusnMenuItem[1].add(item);
                        mMenuItem2.add(child.getReply().displayText);
                    }
                    if(mMenuItem2.size() > 1){
                        mMenuButton2.setCompoundDrawables(left, null, null, null);
                    }
                }
            }
        }
    }

    /**
     * 展示对话框视图，构造方法创建对象
     */
    private CustomSelectDialog showDialog(CustomSelectDialog.SelectDialogListener listener, List<String> names) {
        CustomSelectDialog dialog = new CustomSelectDialog(mActivity,
                R.style.transparentFrameWindowStyle, listener, names);
        dialog.setPopupMenuLayoutParams(mOffsetX, mMenuButton1.getWidth(), mOffsetY);
        Log.i("Junwang", "CustomSelectDialog left="+mMenuButton1.getLeft()+", x="+ mMenuButton1.getX()+", right="
                +mMenuButton1.getRight()+", top="+mMenuButton1.getTop()+", height="+mMenuButton1.getY()+", parent_height="+this.getHeight());
        dialog.setItemColor(R.color.colorAccent,R.color.colorPrimary);
        //判断activity是否finish
        if (/*!this.isFinishing()*/true) {
            dialog.show();
        }
        return dialog;
    }

    public void closeMenu(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.closeOptionsMenu();
        }
    }

    private void showCustomDialog(ArrayList<String> menuItem, int buttonNo) {
        final List<String> names = new ArrayList<>(menuItem);
        if(mDialog != null && mDialog.isShowing()){
            mDialog.closeOptionsMenu();
        }
        mDialog = showDialog(new CustomSelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("junwang", "action_url="+mBusnMenuItem[buttonNo].get(position).getAction_url());
//                onMenuItemClick(mBusnMenuItem[buttonNo].get(position), view);
//                Toast.makeText(getContext(), /*names.get(position)*/mBusnMenuItem[buttonNo].get(position).getAction_url(), Toast.LENGTH_SHORT).show();
            }
        }, names);
    }

    //add by junwang
    private void popupBusnMenu(){
        mPopLayout.setVisibility(View.VISIBLE);
        mMenuView.setOptionMenus(Arrays.asList(
                new OptionMenu("复制"), new OptionMenu("转发到朋友圈"),
                new OptionMenu("收藏"), new OptionMenu("翻译"),
                new OptionMenu("删除")));
        mMenuView.setOrientation(LinearLayout.VERTICAL);

//        Path triangle = new Path();
//        triangle.lineTo(32, 0);
//        triangle.lineTo(16, 16);
//        triangle.close();
//
//        Path path = new Path();
//        path.addRoundRect(new RectF(0, 0, 100, 32), 16, 16, Path.Direction.CW);
//        path.addPath(triangle, 16, 32);
    }
}
