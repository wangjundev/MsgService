package com.stv.msgservice.ui.conversationlist;

import android.view.View;
import android.widget.ImageView;

import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.widget.ProgressFragment;

import java.util.Arrays;
import java.util.List;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

public class ConversationListFragment extends ProgressFragment {
    private RecyclerView recyclerView;
    private ConversationListAdapter adapter;
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
        recyclerView = view.findViewById(R.id.recyclerView);
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
            adapter.setConversationInfos(conversationInfos);
        });
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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
