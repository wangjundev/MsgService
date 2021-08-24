package com.stv.msgservice.ui.channel.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.ui.channel.ChannelChatbotMsgListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelFavoritedMsgFragment extends Fragment {
    @BindView(R2.id.channel_favorited_message_list)
    RecyclerView message_list;
    private ChannelChatbotMsgListAdapter chatbotMsgListAdapter;

    public static ChannelFavoritedMsgFragment newInstance() {
        return new ChannelFavoritedMsgFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreate");
        super.onCreate(savedInstanceState);
        chatbotMsgListAdapter = new ChannelChatbotMsgListAdapter(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ChannelMainFragment onCreateView");
        View view = inflater.inflate(R.layout.channel_favorited_msg_fragment, container, false);
        ButterKnife.bind(this, view);
        initMsgList();
        return view;
    }

    private void initMsgList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        message_list.setLayoutManager(layoutManager);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                this.getActivity().getApplication(), 0);
        MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);

        messageViewModel.getFavoritedMessages().observe(getViewLifecycleOwner(), messageUserInfoEntities -> {
            chatbotMsgListAdapter.setMessageList(messageUserInfoEntities);
            message_list.setAdapter(chatbotMsgListAdapter);
        });
    }
}
