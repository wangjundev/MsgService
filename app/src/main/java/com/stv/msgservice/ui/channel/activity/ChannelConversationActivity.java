package com.stv.msgservice.ui.channel.activity;

import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;

import butterknife.BindView;

public class ChannelConversationActivity extends BaseNoToolbarActivity {
    @BindView(R2.id.portraitImageView)
    ImageView portraitImageView;
    @BindView(R2.id.nameTextView)
    TextView nameTextView;
    ChannelConversationFragment channelConversationFragment;
    @Override
    protected int contentLayout() {
        return R.layout.channel_conversation_activity;
    }

    @Override
    protected void afterViews() {
        channelConversationFragment = ChannelConversationFragment.newInstance();
        init();
        initView();
    }

    public void initView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, channelConversationFragment)
                .commitNow();
    }

    private void init() {
        Intent intent = getIntent();
        long conversationId = intent.getLongExtra("conversationId", 0);
        String chatbotId = intent.getStringExtra("chatbotId");
        String sendAddress = chatbotId;
        String portrait = intent.getStringExtra("chatbotPortrait");
        String name = intent.getStringExtra("chatbotName");
        int isAttentioned = intent.getIntExtra("chatbotIsAttentioned", 0);
        String destinationAddress = intent.getStringExtra("destinationAddress");
        String conversationUUID = intent.getStringExtra("conversationUUID");
        long initialFocusedMessageId = intent.getLongExtra("toFocusMessageId", -1);
        boolean isFromSearch = intent.getBooleanExtra("fromSearch", false);

        nameTextView.setText(name);
        Glide.with(portraitImageView).load(portrait).apply(new RequestOptions().placeholder(R.mipmap.avatar_def)).transform(new CenterCrop(),new GlideCircleWithBorder()).into(portraitImageView);
        channelConversationFragment.setupChannelConversation(conversationId, chatbotId, portrait, name, isAttentioned, sendAddress, destinationAddress, conversationUUID, initialFocusedMessageId, isFromSearch);
        if(conversationId != 0){
//            updateMessagesReadStatus();
        }
    }
}
