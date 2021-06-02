package com.stv.msgservice.ui.conversation.message.search;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.ui.conversation.ConversationActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

public class ConversationMessageSearchModule extends SearchableModule<Message, MessageViewHolder> {
    private Conversation conversation;
    private AppCompatActivity activity;

    public ConversationMessageSearchModule(AppCompatActivity activity, Conversation conversation) {
        this.activity = activity;
        this.conversation = conversation;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(Fragment fragment, @NonNull ViewGroup parent, int type) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item_message, parent, false);
        return new MessageViewHolder(fragment, view);
    }

    @Override
    public void onBind(Fragment fragment, MessageViewHolder holder, Message message) {
        holder.onBind(message);
    }

    @Override
    public int getViewType(Message message) {
        return R.layout.search_item_message;
    }

    @Override
    public void onClick(Fragment fragment, MessageViewHolder holder, View view, Message message) {
        Intent intent = new Intent(fragment.getContext(), ConversationActivity.class);
        intent.putExtra("conversation", /*message.conversation*/(ConversationEntity)conversation);
        intent.putExtra("toFocusMessageId", message.getId());
        fragment.startActivity(intent);
        fragment.getActivity().finish();
    }

    @Override
    public int priority() {
        return 0;
    }

    @Override
    public String category() {
        return "聊天记录";
    }

    @Override
    public List<MessageEntity> search(String keyword) {
        MessageViewModel.Factory factory = new MessageViewModel.Factory(activity.getApplication()
                , 0);
        MessageViewModel messageViewModel = new ViewModelProvider(activity, factory)
                .get(MessageViewModel.class);
        return messageViewModel.searchMessages(keyword);
//        return ChatManager.Instance().searchMessage(conversation, keyword, true, 100, 0);
    }

    @Override
    public boolean expandable() {
        return false;
    }
}
