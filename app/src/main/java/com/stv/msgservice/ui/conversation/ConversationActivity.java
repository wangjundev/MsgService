package com.stv.msgservice.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.stv.msgservice.AppExecutors;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.SendCallback;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.ui.WfcBaseActivity;

import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;

public class ConversationActivity extends WfcBaseActivity {
    private boolean isInitialized = false;
    private ConversationFragment conversationFragment;
    private Conversation conversation;
    private AppExecutors mAppExecutors;
    private SendCallback sendCallback;
    @BindView(R2.id.toolbar_title)
    TextView toolbarTitle;

    @Override
    protected int contentLayout() {
        return R.layout.fragment_container_activity;
    }

    private void setConversationBackground() {
        // you can setup your conversation background here
//        getWindow().setBackgroundDrawableResource(R.mipmap.splash);
    }

    @Override
    protected void afterViews() {
//        IMServiceStatusViewModel imServiceStatusViewModel = ViewModelProviders.of(this).get(IMServiceStatusViewModel.class);
//        imServiceStatusViewModel.imServiceStatusLiveData().observe(this, aBoolean -> {
//            if (!isInitialized && aBoolean) {
//                init();
//                isInitialized = true;
//            }
//        });
//        if(!isInitialized){
//            init();
//            isInitialized = true;
//        }
        Log.i("Junwang", "enter conversationFragment");
        conversationFragment = new ConversationFragment();
        mAppExecutors = new AppExecutors();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.containerFrameLayout, conversationFragment, "content")
                .commit();

        setConversationBackground();
        init();
    }

    @Override
    protected int menu() {
        return R.menu.conversation;
    }

    public ConversationFragment getConversationFragment() {
        return conversationFragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_conversation_info) {
            showConversationInfo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!conversationFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

    public void updateConversationLastMsgId(long newLastMsgId){
        conversation.setLatestMessageId(newLastMsgId);
    }

    public void saveMsg(Context context, String content, String destination, boolean isReceived, String attachmentpath, int messageType){
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            MessageEntity me = mViewModel.saveMsg(context, content, destination, isReceived,  attachmentpath,messageType);
            MessageViewModel.Factory factory = new MessageViewModel.Factory(
                    this.getApplication(), 0);
            MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                    .get(MessageViewModel.class);
            if(content != null && content.length() > 0){
                messageViewModel.sendTextmsg(me, content, null);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
                messageViewModel.updateMessageSendStatus(me);
            }else if(attachmentpath != null){
                messageViewModel.sendFilemsg(me, attachmentpath, null);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
                messageViewModel.updateMessageSendStatus(me);
            }
            conversationFragment.setInitialFocusedMessageId(-1);
        });
    }

    public void resendMsg(MessageEntity messageEntity){
        deleteMsg(messageEntity);
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            String content = messageEntity.getContent();
            String attachmentpath = messageEntity.getAttachmentPath();
            MessageEntity me = mViewModel.saveMsg(this, content, conversation.getNormalizedDestination(), false,  attachmentpath,messageEntity.getMessageType());
            MessageViewModel.Factory factory = new MessageViewModel.Factory(
                    this.getApplication(), 0);
            MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
                    .get(MessageViewModel.class);
            if(content != null && content.length() > 0){
                messageViewModel.sendTextmsg(me, content, null);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
                messageViewModel.updateMessageSendStatus(me);
            }else if(attachmentpath != null){
                messageViewModel.sendFilemsg(me, attachmentpath, null);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
                messageViewModel.updateMessageSendStatus(me);
            }
            conversationFragment.setInitialFocusedMessageId(-1);
        });
    }

    public void updateMessagesReadStatus(){
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                getApplication(), 0);
        MessageViewModel mMessageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);

        mMessageViewModel.getUnReadMessages(conversation.getId()).observe(this, messageEntities ->{
            if((messageEntities != null) && (messageEntities.size() > 0)){
                for(MessageEntity me : messageEntities){
                    me.setRead(0);
                }
                mAppExecutors.diskIO().execute(() -> {
                    conversation.setUnreadCount(0);
                    ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                    mViewModel.updateConversation((ConversationEntity) conversation);
                    mMessageViewModel.updateMessagesReadStatus(messageEntities);
                });
            }
        });
    }

    public void deleteMsgs(List<MessageEntity> messageList){
        conversationFragment.setInitialFocusedMessageId(0);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                getApplication(), 0);
        MessageViewModel mViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);
        mAppExecutors.diskIO().execute(() -> {
//            MessageEntity me = new MessageEntity();
//            for(Message message : messageList){
//                me.setId(message.getId());
//                mViewModel.deleteMessage(me);
//            }
            mViewModel.deleteMessages(messageList);
        });
        boolean isDeleteMsgLastMsg = false;
        for(Message message : messageList){
            Log.i("Junwang","delete msg id="+message.getId()+", lastmsgid="+conversation.getLatestMessageId());
            if(message.getId() == conversation.getLatestMessageId()){
                isDeleteMsgLastMsg = true;
                Log.i("Junwang", "isDeleteMsgLastMsg=true");
                break;
            }
        }
        if(isDeleteMsgLastMsg){
            mViewModel.getMessages(conversation.getId()).observe(this, messageEntities ->{
                if((messageEntities != null) && (messageEntities.size() > 0)){
                    Log.i("Junwang", "message count="+messageEntities.size());
                    MessageEntity messageEntity = messageEntities.get(messageEntities.size()-1);
                    ConversationEntity ce = new ConversationEntity();
                    ce.setLastTimestamp(messageEntity.getTime());
                    ce.setNormalizedDestination(conversation.getNormalizedDestination());
                    ce.setId(conversation.getId());
                    ce.setLatestMessageId(messageEntity.getId());
                    ce.setSnippetText(messageEntity.generateSnippetText());

                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        mConversationListViewModel.updateConversation(ce);
                    });
                }else{
                    Log.i("Junwang", "message count1="+messageEntities.size());
                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        ConversationEntity ce = new ConversationEntity();
                        ce.setId(conversation.getId());
                        mConversationListViewModel.deleteConversation(ce);
                    });
                }
            });
        }
    }
    public void deleteMsg( Message message){
        conversationFragment.setInitialFocusedMessageId(0);
        MessageViewModel.Factory factory = new MessageViewModel.Factory(
                getApplication(), 0);
        MessageViewModel mViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);
        mAppExecutors.diskIO().execute(() -> {
            MessageEntity me = new MessageEntity();
            me.setId(message.getId());
            mViewModel.deleteMessage(me);
        });
        if(message.getId() == conversation.getLatestMessageId()){
            mViewModel.getMessages(conversation.getId()).observe(this, messageEntities ->{
                if((messageEntities != null) && (messageEntities.size() > 0)){
                    Log.i("Junwang", "message count="+messageEntities.size());
                    MessageEntity messageEntity = messageEntities.get(messageEntities.size()-1);
                    ConversationEntity ce = new ConversationEntity();
                    ce.setLastTimestamp(messageEntity.getTime());
                    ce.setNormalizedDestination(conversation.getNormalizedDestination());
                    ce.setId(conversation.getId());
                    ce.setLatestMessageId(messageEntity.getId());
                    ce.setSnippetText(messageEntity.generateSnippetText());

                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        mConversationListViewModel.updateConversation(ce);
                    });
                }else{
                    Log.i("Junwang", "message count1="+messageEntities.size());
                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        ConversationEntity ce = new ConversationEntity();
                        ce.setId(conversation.getId());
                        mConversationListViewModel.deleteConversation(ce);
                    });
                }
            });
        }
    }

    private void showConversationInfo() {
//        Intent intent = new Intent(this, ConversationInfoActivity.class);
//        ConversationInfo conversationInfo = ChatManager.Instance().getConversation(conversation);
//        if (conversationInfo == null) {
//            Toast.makeText(this, "获取会话信息失败", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        intent.putExtra("conversationInfo", conversationInfo);
//        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Junwang", "onNewIntent intent enter.");
        super.onNewIntent(intent);
        conversation = intent.getParcelableExtra("conversation");
        if (conversation == null) {
            finish();
        }
        long initialFocusedMessageId = intent.getLongExtra("toFocusMessageId", -1);
        String channelPrivateChatUser = intent.getStringExtra("channelPrivateChatUser");
        conversationFragment.setupConversation(conversation, null, initialFocusedMessageId, channelPrivateChatUser);
    }

    @Override
    public void setTitle(CharSequence title) {
//        super.setTitle(title);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(title);
    }

    private void init() {
        Intent intent = getIntent();
        conversation = intent.getParcelableExtra("conversation");
        String conversationTitle = intent.getStringExtra("conversationTitle");
        long initialFocusedMessageId = intent.getLongExtra("toFocusMessageId", -1);
        if (conversation == null) {
            finish();
        }
        Log.i("Junwang", "snippet text = "+conversation.getSnippetText()+", lastmsgId="+conversation.getLatestMessageId());
        conversationFragment.setupConversation(conversation, conversationTitle, initialFocusedMessageId, null);
        updateMessagesReadStatus();
    }

//    public static Intent buildConversationIntent(Context context, int type, String target, int line) {
//        return buildConversationIntent(context, type, target, line, -1);
//    }
//
//    public static Intent buildConversationIntent(Context context, int type, String target, int line, long toFocusMessageId) {
//        Conversation conversation = new Conversation(type, target, line);
//        return buildConversationIntent(context, conversation, null, toFocusMessageId);
//    }
//
//    public static Intent buildConversationIntent(Context context, int type, String target, int line, String channelPrivateChatUser) {
//        Conversation conversation = new Conversation(type, target, line);
//        return buildConversationIntent(context, conversation, null, -1);
//    }
//
//    public static Intent buildConversationIntent(Context context, Conversation conversation, String channelPrivateChatUser, long toFocusMessageId) {
//        Intent intent = new Intent(context, ConversationActivity.class);
//        intent.putExtra("conversation", conversation);
//        intent.putExtra("toFocusMessageId", toFocusMessageId);
//        intent.putExtra("channelPrivateChatUser", channelPrivateChatUser);
//        return intent;
//    }
}
