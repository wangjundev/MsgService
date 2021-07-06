package com.stv.msgservice.ui.conversation;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.ui.WfcBaseActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;

public class ConversationActivity extends WfcBaseActivity {
    private boolean isInitialized = false;
    private ConversationFragment conversationFragment;
    public static Conversation conversation;
    private AppExecutors mAppExecutors;
    private SendCallback sendCallback;
    @BindView(R2.id.toolbar_title)
    TextView toolbarTitle;
    private MessageViewModel.Factory factory;
    private MessageViewModel mMessageViewModel;

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
        factory = new MessageViewModel.Factory(
                this.getApplication(), 0);
        mMessageViewModel = new ViewModelProvider(this, factory)
                .get(MessageViewModel.class);
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
        if(conversation != null){
            return R.menu.conversation;
        }else{
            return 0;
        }
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

    public void saveLocationMsg(Context context, String content, String destination, boolean isReceived, int messageType, LocationData locationData){
        conversationFragment.setInitialFocusedMessageId(-1);
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            MessageEntity me = mViewModel.saveLocationMsg(context, content, destination, isReceived, messageType, locationData);
            mMessageViewModel.sendLocationMessage(me, locationData);
            mMessageViewModel.updateMessageSendStatus(me);
        });
    }

    public void updateMesasge(MessageEntity me){
        mAppExecutors.diskIO().execute(() -> {
            mMessageViewModel.updateMessageSendStatus(me);
        });
    }

    public void saveMsg(Context context, String content, String from, String to, String conversationId, boolean isReceived, String attachmentpath, String thumbnail, int messageType, String attachmentType){
        conversationFragment.setInitialFocusedMessageId(-1);
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            if(conversation == null){
                conversation = new ConversationEntity();
            }
            MessageEntity me = mViewModel.saveMsg(context, content, to, from, conversationId, isReceived,  attachmentpath, thumbnail, messageType, (ConversationEntity) conversation, attachmentType);
            if(conversationFragment.msgLiveData != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversationFragment.msgLiveData.setValue(me);
                    }
                });
            }
            if(content != null && content.length() > 0){
                mMessageViewModel.sendTextmsg(context, from, to, conversationId, me, content, null, conversationFragment.msgLiveData, conversationFragment.msgUpdateLiveData);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
            }else if(attachmentpath != null){
                mMessageViewModel.sendFilemsg(me, from, to, conversationId, attachmentpath, null, conversationFragment.msgLiveData, conversationFragment.msgUpdateLiveData);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
            }
        });
    }

//    public void saveMsg(Context context, String content, String destination, boolean isReceived, String attachmentpath, String thumbnail, int messageType){
//        conversationFragment.setInitialFocusedMessageId(-1);
//        mAppExecutors.diskIO().execute(() -> {
//            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
//            MessageEntity me = mViewModel.saveMsg(context, content, destination, isReceived,  attachmentpath, thumbnail, messageType);
//            if(content != null && content.length() > 0){
//                mMessageViewModel.sendTextmsg(context, conversation, me, content, null);
//                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
//            }else if(attachmentpath != null){
//                mMessageViewModel.sendFilemsg(me, attachmentpath, null);
//                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
//            }
//        });
//    }

    public void resendMsg(MessageEntity messageEntity){
        deleteMsg(messageEntity);
        conversationFragment.setInitialFocusedMessageId(-1);
        mAppExecutors.diskIO().execute(() -> {
            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
            String content = messageEntity.getContent();
            String attachmentpath = messageEntity.getAttachmentPath();
            String thumbnail = messageEntity.getThumbnailPath();
            MessageEntity me = mViewModel.saveMsg(this, content, conversation.getSenderAddress(), conversation.getDestinationAddress(), conversation.getConversationID(), false,  attachmentpath, thumbnail, messageEntity.getMessageType(), (ConversationEntity) conversation, messageEntity.getAttachmentType());
            if(conversationFragment.msgLiveData != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversationFragment.msgLiveData.setValue(me);
                    }
                });
            }
            if(content != null && content.length() > 0){
                mMessageViewModel.sendTextmsg(this, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), me, content, null, conversationFragment.msgLiveData, conversationFragment.msgUpdateLiveData);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
            }else if(attachmentpath != null){
                mMessageViewModel.sendFilemsg(me, conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationID(), attachmentpath, null, conversationFragment.msgLiveData, conversationFragment.msgUpdateLiveData);
                Log.i("Junwang", "msgid = "+me.getId()+" update message status="+me.getMessageStatus());
//                mMessageViewModel.updateMessageSendStatus(me);
            }
        });
    }

    public void sendReadReport(MessageEntity me){
        mMessageViewModel.sendReadReport(me, conversation.getSenderAddress(), conversation.getDestinationAddress());
    }

    public void updateMessagesReadStatus(){
        List<MessageEntity> messageEntityList = new ArrayList<>();
        mMessageViewModel.getUnReadMessages(conversation.getId()).observe(this, messageEntities ->{
            if((messageEntities != null) && (messageEntities.size() > 0)){
                for(MessageEntity me : messageEntities){
                    messageEntityList.add(me);
                    me.setRead(0);
                }
                mAppExecutors.diskIO().execute(() -> {
                    conversation.setUnreadCount(0);
                    ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                    mViewModel.updateConversation((ConversationEntity) conversation);
                    mMessageViewModel.updateMessagesReadStatus(messageEntities);

                    if(messageEntityList != null && messageEntityList.size() > 0){
                        for(MessageEntity me : messageEntityList){
                            sendReadReport(me);
                        }
                    }
                });
            }
        });
    }

    public void deleteMsgs(List<MessageEntity> messageList){
        conversationFragment.setInitialFocusedMessageId(0);
        mAppExecutors.diskIO().execute(() -> {
//            MessageEntity me = new MessageEntity();
//            for(Message message : messageList){
//                me.setId(message.getId());
//                mViewModel.deleteMessage(me);
//            }
            mMessageViewModel.deleteMessages(messageList);
        });
        boolean isDeleteMsgLastMsg = false;
        for(Message message : messageList){
            Log.i("Junwang","delete msg id="+message.getId()+", lastmsgid="+conversation.getLatestMessageId());
            conversationFragment.adapter.removeMessage(message);
            if(message.getId() == conversation.getLatestMessageId()){
                isDeleteMsgLastMsg = true;
                Log.i("Junwang", "isDeleteMsgLastMsg=true");
//                break;
            }
        }
        if(isDeleteMsgLastMsg){
            mMessageViewModel.getMessages(conversation.getId()).observe(this, messageEntities ->{
                if((messageEntities != null) && (messageEntities.size() > 0)){
                    Log.i("Junwang", "message count="+messageEntities.size());
                    MessageEntity messageEntity = messageEntities.get(messageEntities.size()-1);
                    ConversationEntity ce = new ConversationEntity();
                    ce.setLastTimestamp(messageEntity.getTime());
                    ce.setNormalizedDestination(conversation.getNormalizedDestination());
                    ce.setId(conversation.getId());
                    ce.setDestinationAddress(conversation.getDestinationAddress());
                    ce.setSenderAddress(conversation.getSenderAddress());
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
        mAppExecutors.diskIO().execute(() -> {
            MessageEntity me = new MessageEntity();
            me.setId(message.getId());
            mMessageViewModel.deleteMessage(me, conversationFragment.msgRemovedLiveData);
        });
        if(message.getId() == conversation.getLatestMessageId()){
            mMessageViewModel.getMessages(conversation.getId()).observe(this, messageEntities ->{
                if((messageEntities != null) && (messageEntities.size() > 0)){
                    Log.i("Junwang", "message count="+messageEntities.size());
                    MessageEntity messageEntity = messageEntities.get(messageEntities.size()-1);
                    ConversationEntity ce = new ConversationEntity();
                    ce.setLastTimestamp(messageEntity.getTime());
                    ce.setNormalizedDestination(conversation.getNormalizedDestination());
                    ce.setDestinationAddress(conversation.getDestinationAddress());
                    ce.setSenderAddress(conversation.getSenderAddress());
                    ce.setId(conversation.getId());
                    ce.setLatestMessageId(messageEntity.getId());
                    ce.setSnippetText(messageEntity.generateSnippetText());
                    conversationFragment.adapter.removeMessage(message);
//                    conversationFragment.adapter.notifyDataSetChanged();
                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        mConversationListViewModel.updateConversation(ce);
                    });
                }else{
                    Log.i("Junwang", "message count1="+messageEntities.size());
                    conversationFragment.adapter.removeMessage(message);
                    mAppExecutors.diskIO().execute(() -> {
                        ConversationListViewModel mConversationListViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
                        ConversationEntity ce = new ConversationEntity();
                        ce.setId(conversation.getId());
                        mConversationListViewModel.deleteConversation(ce);
                    });
                }
            });
        }else{
            conversationFragment.adapter.removeMessage(message);
//            conversationFragment.adapter.notifyDataSetChanged();
        }
    }

    private void showConversationInfo() {
        Intent intent = new Intent(/*getActivity()*/this, ConversationInfoActivity.class);
        if (conversation == null) {
            Toast.makeText(this, "获取会话信息失败", Toast.LENGTH_SHORT).show();
            return;
        }
        intent.putExtra("conversation", (ConversationEntity)conversation);
        intent.putExtra("activityname", "com.stv.msgservice.ui.conversation.ConversationActivity");
        startActivity(intent);
//        Intent intent = new Intent(this, ConversationInfoActivity.class);
//        ConversationInfo conversationInfo = ChatManager.Instance().getConversation(conversation);
//        if (conversationInfo == null) {
//            Toast.makeText(this, "获取会话信息失败", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        intent.putExtra("conversationInfo", conversationInfo);
//        startActivity(intent);
//        MessageViewModel.Factory factory = new MessageViewModel.Factory(
//                this.getApplication(), 0);
//        MessageViewModel messageViewModel = new ViewModelProvider(this, factory)
//                .get(MessageViewModel.class);
//        messageViewModel.searchMessages("大").observe(this, messageEntityList -> {
//            if(messageEntityList != null && messageEntityList.size() > 0){
//                for(int i=0; i<messageEntityList.size(); i++){
//                    Log.i("Junwang", "matched messageid="+messageEntityList.get(i).getId()+", content="+messageEntityList.get(i).getContent());
//                }
//            }
//        });
    }

    public void setConversationTopStatus(boolean isTop){
//        Log.i("Junwang", "setConversationTopStatus isTop="+isTop+", id="+conversation.getId());
        conversation.setTop(isTop);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("Junwang", "onNewIntent intent enter.");
        super.onNewIntent(intent);
//        conversation = intent.getParcelableExtra("conversation");
//        String chatbotId = intent.getStringExtra("chatbotId");
//        String title = intent.getStringExtra("conversationTitle");
////        if (conversation == null) {
////            finish();
////        }
//        long initialFocusedMessageId = intent.getLongExtra("toFocusMessageId", -1);
//        String channelPrivateChatUser = intent.getStringExtra("channelPrivateChatUser");
//        boolean isFromSearch = intent.getBooleanExtra("fromSearch", false);
//        if(conversationFragment.getAdapter() != null){
//            Log.i("Junwang", "onNewIntent notifyDataSetChanged");
//            conversationFragment.getAdapter().notifyDataSetChanged();
//        }
////        if(isFromSearch) {
////            conversationFragment.onDestroy();
////        }
//        conversationFragment.setupConversation(conversation, chatbotId, title, initialFocusedMessageId, channelPrivateChatUser, isFromSearch);
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
        String chatbotId = intent.getStringExtra("chatbotId");
        String conversationTitle = intent.getStringExtra("conversationTitle");
        long initialFocusedMessageId = intent.getLongExtra("toFocusMessageId", -1);
        boolean isFromSearch = intent.getBooleanExtra("fromSearch", false);
//        if (conversation == null && !isFromSearch) {
//            finish();
//        }
//        Log.i("Junwang", "snippet text = "+conversation.getSnippetText()+", lastmsgId="+conversation.getLatestMessageId());
        conversationFragment.setupConversation(conversation, chatbotId, conversationTitle, initialFocusedMessageId, null, isFromSearch);
        if(conversation != null){
            updateMessagesReadStatus();
        }
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
