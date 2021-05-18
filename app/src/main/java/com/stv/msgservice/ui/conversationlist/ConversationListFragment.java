package com.stv.msgservice.ui.conversationlist;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.widget.ProgressFragment;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class ConversationListFragment extends ProgressFragment {
    private SwipeRecyclerView mRecyclerView;
    private ConversationListAdapter adapter;
    private SwipeMenuCreator swipeMenuCreator;
    private OnItemMenuClickListener mMenuItemClickListener;
    private List<ConversationEntity> conversations;

//    private static final List<Conversation.ConversationType> types = Arrays.asList(Conversation.ConversationType.Single,
//            Conversation.ConversationType.Group,
//            Conversation.ConversationType.Channel);
    private static final List<Integer> lines = Arrays.asList(0);

    private ConversationListViewModel conversationListViewModel;
//    private SettingViewModel settingViewModel;
    private LinearLayoutManager layoutManager;

    @Override
    protected int contentLayout() {
        return R.layout.conversationlist_frament;
    }

    @Override
    protected void afterViews(View view) {
        mRecyclerView = view.findViewById(R.id.recyclerView);
        ImageView simulate_btn = view.findViewById(R.id.simulate_btn);
        simulate_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                ((MainActivity) requireActivity()).simulateReceivedMsg();
            }
        });
        init();
    }

    public static ConversationListFragment newInstance() {
        return new ConversationListFragment();
    }
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (adapter != null && isVisibleToUser) {
//            reloadConversations();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        reloadConversations();
    }

    private void init() {
        adapter = new ConversationListAdapter(this);
//        conversationListViewModel = new ViewModelProvider(this, new ConversationListViewModelFactory(types, lines))
//                .get(ConversationListViewModel.class);
//        conversationListViewModel.conversationListLiveData().observe(this, conversationInfos -> {
//            showContent();
//            adapter.setConversationInfos(conversationInfos);
//        });
        conversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
        conversationListViewModel.getConversations().observe(this, conversationInfos -> {
            showContent();
            conversations = conversationInfos;
            adapter.setConversationInfos(conversationInfos);
        });

        /**
         * 菜单创建器，在Item要创建菜单的时候调用。
         */
        swipeMenuCreator = new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
                int height = ViewGroup.LayoutParams.MATCH_PARENT;
                SwipeMenuItem deleteItem = new SwipeMenuItem(getContext())
                        .setText("删除")
                        .setTextSize(16)
                        .setBackgroundColor(getResources().getColor(R.color.red0))
                        .setWidth(36*5)
                        .setHeight(height);
//                SwipeMenuItem topItem = new SwipeMenuItem(getContext())
//                        .setText("置顶")
//                        .setHeight(height)
//                        .setWidth(36*5)
//                        .setBackgroundColor(getResources().getColor(R.color.green1));
                swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
//                swipeRightMenu.addMenuItem(topItem);
            }
        };

        /**
         * RecyclerView的Item的Menu点击监听。
         */
        mMenuItemClickListener = new OnItemMenuClickListener() {
            @Override
            public void onItemClick(SwipeMenuBridge menuBridge, int position) {
                LogUtil.i("Junwang", "ChatbotFavoriteActivity OnItemMenuClickListener position "+ position+" clicked.");
                menuBridge.closeMenu();

                int direction = menuBridge.getDirection();
                int menuPosition = menuBridge.getPosition();

                if (direction == SwipeRecyclerView.RIGHT_DIRECTION) {
                    if(menuPosition == 0){
                        Log.i("Junwang", "选中删除第"+position+"条会话.");
                        ((MainActivity)getActivity()).deleteConversation(conversations.get(position));
                    }else if(menuPosition == 1){
                        //置顶
                    }
                }
            }
        };

        mRecyclerView.setSwipeMenuCreator(swipeMenuCreator);
        mRecyclerView.setOnItemMenuClickListener(mMenuItemClickListener);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

//        UserViewModel userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
//        userViewModel.userInfoLiveData().observe(this, new Observer<List<UserInfo>>() {
//            @Override
//            public void onChanged(List<UserInfo> userInfos) {
//                int start = layoutManager.findFirstVisibleItemPosition();
//                int end = layoutManager.findLastVisibleItemPosition();
//                adapter.notifyItemRangeChanged(start, end - start + 1);
//            }
//        });
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                requireActivity().getApplication(), null);

        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(this, factory)
                .get(UserInfoViewModel.class);
//        UserInfoViewModel userInfoViewModel = new ViewModelProvider(this).get(UserInfoViewModel.class);
        userInfoViewModel.getUsers().observe(this, new Observer<List<UserInfoEntity>>() {
            @Override
            public void onChanged(List<UserInfoEntity> userInfos) {
                int start = layoutManager.findFirstVisibleItemPosition();
                int end = layoutManager.findLastVisibleItemPosition();
                adapter.notifyItemRangeChanged(start, end - start + 1);
            }
        });
    }

    private void reloadConversations() {
//        if (ChatManager.Instance().getConnectionStatus() == ConnectionStatus.ConnectionStatusReceiveing) {
//            return;
//        }
        conversationListViewModel.loadConversations();
//        conversationListViewModel.reloadConversationUnreadStatus();
    }

    public void removeConversation(long conversationId){
        conversationListViewModel.deleteConversation(conversationId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
