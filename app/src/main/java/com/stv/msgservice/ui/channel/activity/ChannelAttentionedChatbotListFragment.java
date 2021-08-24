package com.stv.msgservice.ui.channel.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelAttentionedChatbotListFragment extends Fragment {
    @BindView(R2.id.channel_attentioned_chatbot_list)
    SwipeRecyclerView channel_attentioned_chatbot_list;
    private ChannelAttentionedChatbotListAdapter attentionedChatbotListAdapter;
    private SwipeMenuCreator swipeMenuCreator;
    private OnItemMenuClickListener mMenuItemClickListener;
    private UserInfoViewModel.Factory factory;
    private UserInfoViewModel userInfoViewModel;
    private List<UserInfoEntity> userInfos;

    public static ChannelAttentionedChatbotListFragment newInstance() {
        return new ChannelAttentionedChatbotListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreate");
        super.onCreate(savedInstanceState);
        attentionedChatbotListAdapter = new ChannelAttentionedChatbotListAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreateView");
        View view = inflater.inflate(R.layout.channel_attentioned_chatbot_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initMsgList();
        return view;
    }

    private void initMsgList(){
        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                        .setText("取消关注")
                        .setTextAppearance(R.style.slidemenu)
//                        .setTextSize(16)
//                        .setTextColor(Color.WHITE)
                        .setBackgroundColor(Color.parseColor("#F25353"))
                        .setWidth(112*3)
                        .setHeight(height);
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        };
        mMenuItemClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                LogUtil.i("Junwang", "ChatbotFavoriteActivity OnItemMenuClickListener position "+ position+" clicked.");
                menuBridge.closeMenu();

                int direction = menuBridge.getDirection();
                int menuPosition = menuBridge.getPosition();

                if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                    if(menuPosition == 0){
                        Log.i("Junwang", "选中删除第"+position+"条关注的应用号.");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                userInfoViewModel.updateAttentionByChatbotId(userInfos.get(position).getUri(), 0);
                            }
                        }).start();
                    }
                }
            }
        };
        channel_attentioned_chatbot_list.setSwipeMenuCreator(swipeMenuCreator);
        channel_attentioned_chatbot_list.setOnItemMenuClickListener(mMenuItemClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        channel_attentioned_chatbot_list.setLayoutManager(layoutManager);
        factory = new UserInfoViewModel.Factory(
                getActivity().getApplication(), null);

        userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);

        userInfoViewModel.getAttentionedChatbotList().observe(getViewLifecycleOwner(), userInfoEntities -> {
            userInfos = userInfoEntities;
            attentionedChatbotListAdapter.setMembers(userInfoEntities);
            channel_attentioned_chatbot_list.setAdapter(attentionedChatbotListAdapter);
        });
    }
}
