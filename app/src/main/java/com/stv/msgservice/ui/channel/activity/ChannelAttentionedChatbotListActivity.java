package com.stv.msgservice.ui.channel.activity;

import android.widget.TextView;

import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.ui.conversation.message.search.BaseNoToolbarActivity;

import butterknife.BindView;

public class ChannelAttentionedChatbotListActivity extends BaseNoToolbarActivity {
    @BindView(R2.id.tv_title)
    TextView tv_title;

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
        tv_title.setText("关注的应用号");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, /*ConversationListFragment.newInstance()*/ChannelAttentionedChatbotListFragment.newInstance())
                .commitNow();
    }
}
