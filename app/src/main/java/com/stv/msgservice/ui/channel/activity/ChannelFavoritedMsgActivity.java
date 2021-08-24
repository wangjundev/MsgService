package com.stv.msgservice.ui.channel.activity;

import com.stv.msgservice.R;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;

public class ChannelFavoritedMsgActivity extends BaseNoToolbarActivity {

    @Override
    protected int contentLayout() {
        return R.layout.channel_favorited_msg_activity;
    }

    @Override
    protected void afterViews() {
//        setStatusBar();
//        chatbotMsgListAdapter = new ChannelChatbotMsgListAdapter(this);
        initView();
    }

    public void initView(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, /*ConversationListFragment.newInstance()*/ChannelFavoritedMsgFragment.newInstance())
                .commitNow();
    }
}
