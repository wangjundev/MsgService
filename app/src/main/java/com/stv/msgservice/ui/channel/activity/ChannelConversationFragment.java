package com.stv.msgservice.ui.channel.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuEntity;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.conversation.ChatbotIntroduceActivity;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ConversationMessageAdapter;
import com.stv.msgservice.ui.conversation.menu.ThreeButtonPopupMenuView;
import com.stv.msgservice.ui.conversation.menu.TwoButtonPopupMenuView;
import com.stv.msgservice.ui.conversation.message.UiMessage;
import com.stv.msgservice.ui.conversation.message.multimsg.MultiMessageAction;
import com.stv.msgservice.ui.conversation.message.multimsg.MultiMessageActionManager;
import com.stv.msgservice.ui.widget.InputAwareLayout;
import com.stv.msgservice.ui.widget.KeyboardAwareLinearLayout;

import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class ChannelConversationFragment extends Fragment implements
        KeyboardAwareLinearLayout.OnKeyboardShownListener,
        KeyboardAwareLinearLayout.OnKeyboardHiddenListener,
        ChannelConversationMessageAdapter.OnPortraitClickListener,
        ChannelConversationMessageAdapter.OnPortraitLongClickListener,
        ChannelConversationInputPanel.OnConversationInputPanelStateChangeListener,
        ChannelConversationMessageAdapter.OnMessageCheckListener {

    private static final String TAG = "ChannelConvFragment";

    public long conversationId;
    private String chatbotId;
    private String chatbotPortrait;
    private String chatbotName;
    private int chatbotIsAttentioned;
    private String sendAddress;
    private String destinationAddress;
    private String conversationUUID;
    private boolean loadingNewMessage;
    private boolean shouldContinueLoadNewMessage = false;

    private static final int MESSAGE_LOAD_COUNT_PER_TIME = 20;
    private static final int MESSAGE_LOAD_AROUND = 10;

    @BindView(R2.id.rootLinearLayout)
    InputAwareLayout rootLinearLayout;
    @BindView(R2.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R2.id.msgRecyclerView)
    RecyclerView recyclerView;

    @BindView(R2.id.inputPanelFrameLayout)
    FrameLayout inputPanelFrameLayout;
    @BindView(R2.id.inputPanel)
    ChannelConversationInputPanel inputPanel;

    @BindView(R2.id.multiMessageActionContainerLinearLayout)
    LinearLayout multiMessageActionContainerLinearLayout;

    private ImageView mSwitchButton;
    @BindView(R2.id.two_button_menu_container)
    TwoButtonPopupMenuView mTwoButtonMenu;
    @BindView(R2.id.three_button_menu_container)
    ThreeButtonPopupMenuView mThreeButtonMenu;
    private int mMenuCount;
    private ChatbotMenuEntity mChatbotMenuEntity;
    private boolean mIsSwitchOnClickListenerSet;
    private int menuNumber;
    public MutableLiveData<MessageEntity> msgUpdateLiveData;
    public MutableLiveData<MessageEntity> msgLiveData;
    public MutableLiveData<Message> msgRemovedLiveData;

    public ChannelConversationMessageAdapter adapter;
    private boolean moveToBottom = true;
    private MessageViewModel messageViewModel;
    private UserInfoViewModel userViewModel;

    private Handler handler;
    private long initialFocusedMessageId;
    private long firstUnreadMessageId;
    // 用户channel主发起，针对某个用户的会话
    private String channelPrivateChatUser;
    private String conversationTitle = "";
    private boolean isFromSearch;
    private LinearLayoutManager layoutManager;

    public static ChannelConversationFragment newInstance() {
        return new ChannelConversationFragment();
    }

    private Observer<MessageEntity> messageLiveDataObserver = new Observer<MessageEntity>() {
        @Override
        public void onChanged(@Nullable MessageEntity uiMessage) {
            if (/*isDisplayableMessage(uiMessage)*/true) {
                // 消息定位时，如果收到新消息、或者发送消息，需要重新加载消息列表
                if (shouldContinueLoadNewMessage) {
                    shouldContinueLoadNewMessage = false;
                    reloadMessage();
                    return;
                }
                adapter.addNewMessage(uiMessage);
                int position = adapter.getItemCount() - 1;
                if (position < 0) {
                    return;
                }
                recyclerView.scrollToPosition(position);
            }
            resetConversationTitle();
            if(conversationId != 0){
                messageViewModel.clearUnreadStatus(conversationId);
            }
        }
    };
    private Observer<MessageEntity> messageUpdateLiveDatObserver = new Observer<MessageEntity>() {
        @Override
        public void onChanged(@Nullable MessageEntity uiMessage) {
            Log.i("Junwang", "messageUpdateLiveDatObserver onChanged");
            if(conversationId == 0){
                adapter.updateMessage(uiMessage);
            }
            ((ConversationActivity)getActivity()).updateMesasge(uiMessage);
        }
    };

    private Observer<Message> messageRemovedLiveDataObserver = new Observer<Message>() {
        @Override
        public void onChanged(@Nullable Message uiMessage) {
            if(uiMessage != null){
                adapter.removeMessage(uiMessage);
            }
        }
    };

    private boolean isDisplayableMessage(Message uiMessage) {
        return uiMessage.getId() != 0;
    }

    private Observer<Map<String, String>> mediaUploadedLiveDataObserver = new Observer<Map<String, String>>() {
        @Override
        public void onChanged(@Nullable Map<String, String> stringStringMap) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("sticker", Context.MODE_PRIVATE);
            for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
                sharedPreferences.edit()
                        .putString(entry.getKey(), entry.getValue())
                        .apply();
            }

        }
    };

    private Observer<List<UserInfo>> userInfoUpdateLiveDataObserver = new Observer<List<UserInfo>>() {
        @Override
        public void onChanged(@Nullable List<UserInfo> userInfos) {
            if (conversationId == 0) {
                return;
            }
            conversationTitle = null;
            setTitle();

            int start = layoutManager.findFirstVisibleItemPosition();
            int end = layoutManager.findLastVisibleItemPosition();
            adapter.notifyItemRangeChanged(start, end - start + 1, userInfos);
        }
    };

    private boolean isMessageInCurrentConversation(Message message) {
        if (conversationId == 0 || message == null) {
            return false;
        }
        return conversationId == message.getConversationId();
    }

    public ChannelConversationInputPanel getConversationInputPanel() {
        return inputPanel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("Junwang", "ConversationFragment onCreate");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.channel_conversation_fragment, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (messageViewModel != null && conversationId != 0) {
            messageViewModel.clearUnreadStatus(conversationId);
        }
    }

    public void setupChannelConversation(long conversationId, String chatbotId, String chatbotPortrait, String chatbotName, int chatbotIsAttentioned,
                                         String senderAddress, String destinationAddress, String conversationUUID, long focusMessageId, boolean isFromSearch) {
        this.conversationId = conversationId;
        this.chatbotId = chatbotId;
        this.chatbotPortrait = chatbotPortrait;
        this.chatbotName = chatbotName;
        this.chatbotIsAttentioned = chatbotIsAttentioned;
        this.sendAddress = senderAddress;
        this.destinationAddress = destinationAddress;
        this.conversationUUID = conversationUUID;
        this.initialFocusedMessageId = focusMessageId;
        this.isFromSearch = isFromSearch;
    }

    private final ChatbotMenuEntity getChatbotMenuEntity(String json){
        ChatbotMenuEntity menuEntity = null;
        try{
            menuEntity = new GsonBuilder().setLenient().create().fromJson(json, ChatbotMenuEntity.class);
//            menuEntity = new Gson().fromJson(json, new TypeToken<ChatbotMenuEntity>(){}.getType());
        }catch(Exception e){
            Log.e("Junwang", "parse chatbot menu json exception: "+e.toString());
        }
        return menuEntity;
    }

    public void startMenuSwitchAnimation(View swithchOut, View swithchIn){
        ObjectAnimator translationY;
        translationY = ObjectAnimator.ofFloat(swithchOut, "translationY", 0f, 200.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(translationY);
        animatorSet.setDuration(100);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator translationY1;
//                if(!(swithchOut instanceof ConversationInputPanel)){
//                    swithchOut.setVisibility(View.GONE);
//                }
                translationY1 = ObjectAnimator.ofFloat(swithchIn, "translationY", 200f, 0f);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationY1);
                animatorSet.setDuration(100);
                animatorSet.start();
                swithchIn.setVisibility(View.VISIBLE);
                swithchOut.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSet.start();
    }

    public void setSwitchButton(String chatbotMenu){
        if(chatbotMenu != null){
            Log.i("Junwang", "setSwitchButton chatbotMenu = "+chatbotMenu);
            mChatbotMenuEntity = getChatbotMenuEntity(chatbotMenu);
            if(mChatbotMenuEntity != null){
                int i= 0;
                for(;i<mChatbotMenuEntity.getMenu().getEntries().length;i++){
                    i++;
                }
                mMenuCount = i;
            }else{
                mMenuCount = 0;
            }

        }else{
            mMenuCount = 0;
        }
        mSwitchButton = inputPanel.switchImageView;
        final View mDivider = (View) inputPanel.findViewById(R.id.button_divider_margin);
        if(mChatbotMenuEntity != null){
            menuNumber = mChatbotMenuEntity.getMenu().getEntries().length;
            Log.i("Junwang", "menuNumber="+menuNumber);
            if(menuNumber > 0){
                mSwitchButton.setVisibility(View.VISIBLE);
                if(!mIsSwitchOnClickListenerSet) {
                    mIsSwitchOnClickListenerSet = true;
                    mSwitchButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View clickView) {
                            Log.i("junwang", "switch button onClicked.");
//                            inputPanel.setVisibility(View.GONE);
                            Log.i("Junwang", "menuNumber=" + menuNumber);
                            if (menuNumber == 2) {
                                startMenuSwitchAnimation(inputPanel, mTwoButtonMenu);
                            } else if (menuNumber == 3) {
                                Log.i("Junwang", "start switch from compose to menu");
                                startMenuSwitchAnimation(inputPanel, mThreeButtonMenu);
                            }
                        }
                    });
                    mDivider.setVisibility(View.VISIBLE);
                    if (menuNumber == 2) {
//                        mTwoButtonMenu = (TwoButtonPopupMenuView) mBinding.twoButtonMenuContainer;
                        final View mTwoBtnSwitchButton = (ImageView) mTwoButtonMenu.findViewById(R.id.switch_to_composemsg);
                        if (mChatbotMenuEntity != null) {
                            mTwoButtonMenu.setMenu(getActivity(), mChatbotMenuEntity);
                        }
                        mTwoBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View clickView) {
                                Log.i("junwang", "mTwoBtnSwitchButton onClicked.");
                                mTwoButtonMenu.closeMenu();
                                startMenuSwitchAnimation(mTwoButtonMenu, inputPanel);
//                                mTwoButtonMenu.setVisibility(View.GONE);
                            }
                        });
                        inputPanel.setVisibility(View.GONE);
                        mTwoButtonMenu.setVisibility(View.VISIBLE);
                    } else if (menuNumber == 3) {
//                        mThreeButtonMenu = (ThreeButtonPopupMenuView) mBinding.threeButtonMenuContainer;
                        final View mThreeBtnSwitchButton = (ImageView) mThreeButtonMenu.findViewById(R.id.switch_to_composemsg);
                        if (mChatbotMenuEntity != null) {
                            mThreeButtonMenu.setMenu(getActivity(), mChatbotMenuEntity);
                        }
                        mThreeBtnSwitchButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View clickView) {
                                Log.i("junwang", "mThreeBtnSwitchButton onClicked.");
                                mThreeButtonMenu.closeMenu();
                                startMenuSwitchAnimation(mThreeButtonMenu, inputPanel);
//                                mThreeButtonMenu.setVisibility(View.GONE);
                            }
                        });
                        inputPanel.setVisibility(View.GONE);
                        mThreeButtonMenu.setVisibility(View.VISIBLE);
                    }
                }
            }else {
                mSwitchButton.setVisibility(View.GONE);
                mDivider.setVisibility(View.GONE);
            }
        }else{
            mSwitchButton.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        }
        //add by junwang for chatbot menu end
    }

    private void initView() {
        handler = new Handler();
        rootLinearLayout.addOnKeyboardShownListener(this);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (adapter.getMessages() == null || adapter.getMessages().isEmpty()) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            loadMoreOldMessages();
        });

        // message list
        adapter = new ChannelConversationMessageAdapter(this, chatbotPortrait, chatbotName, chatbotIsAttentioned);
        adapter.setOnPortraitClickListener(this);
//        adapter.setOnMessageReceiptClickListener(this);
        adapter.setOnPortraitLongClickListener(this);
        adapter.setOnMessageCheckListener(this);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("Junwang", "recyclerview newState="+newState);
                boolean IsScrolling = false;
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    IsScrolling = true;
                    Log.i("Junwang", "glide pauseRequests.");
                    Glide.with(getActivity()).pauseRequests();
                    return;
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (IsScrolling == true) {
//                        Glide.with(getActivity()).resumeRequests();
                    }
                    IsScrolling = false;
                    return;
                }
                // 向上滑动，不在底部，收到消息时，不滑动到底部, 发送消息时，可以强制置为true
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                Log.i("Junwang", "recyclerView.canScrollVertically");
                if (!recyclerView.canScrollVertically(1)) {
                    moveToBottom = true;
                    if ((initialFocusedMessageId != -1 || firstUnreadMessageId != 0) && !loadingNewMessage && shouldContinueLoadNewMessage) {
                        int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                        if (lastVisibleItem > adapter.getItemCount() - 3) {
                            loadMoreNewMessages();
                        }
                    }
                } else {
                    moveToBottom = false;
                }
            }
        });

        inputPanel.init(this, rootLinearLayout);
        inputPanel.setOnConversationInputPanelStateChangeListener(this);
        setupConversation();

        if(messageViewModel == null){
            MessageViewModel.Factory factory = new MessageViewModel.Factory(
                    this.getActivity().getApplication(), 0);

            messageViewModel = new ViewModelProvider(this, factory)
                    .get(MessageViewModel.class);
        }

        if(/*conversation == null*/true){
            msgLiveData = messageViewModel.messageLiveData();
            msgUpdateLiveData = messageViewModel.messageUpdateLiveData();
            msgRemovedLiveData = messageViewModel.messageRemovedLiveData();
            msgLiveData.observeForever(messageLiveDataObserver);
            msgUpdateLiveData.observeForever(messageUpdateLiveDatObserver);
            msgRemovedLiveData.observeForever(messageRemovedLiveDataObserver);
//            messageViewModel.mediaUpdateLiveData().observeForever(mediaUploadedLiveDataObserver);
        }
        if(userViewModel == null ){
            UserInfoViewModel.Factory factory1 = new UserInfoViewModel.Factory(
                    this.getActivity().getApplication(), null);
            userViewModel = new ViewModelProvider(this, factory1)
                    .get(UserInfoViewModel.class);
        }

        userViewModel.userInfoLiveData().observeForever(userInfoUpdateLiveDataObserver);
    }

    private void setupConversation() {
        inputPanel.setupConversation(conversationId, chatbotId, sendAddress, destinationAddress, conversationUUID);
        loadMessage(initialFocusedMessageId);

        setTitle();
    }

    private void setupConversation(long conversationId, String chatbotId, String sendAddress, String destinationAddress, String conversationUUID){
        this.conversationId = conversationId;
        this.chatbotId = chatbotId;
        this.sendAddress = sendAddress;
        this.destinationAddress = destinationAddress;
        this.conversationUUID = conversationUUID;
    }

    public void setInitialFocusedMessageId(long initialFocusedMessageId){
        this.initialFocusedMessageId = initialFocusedMessageId;
    }


    private void loadMessage(long focusMessageId) {

        LiveData<List<MessageEntity>> messages;
//        if (focusMessageId != -1) {
//            shouldContinueLoadNewMessage = true;
//            messages = conversationViewModel.loadAroundMessages(conversation, channelPrivateChatUser, focusMessageId, MESSAGE_LOAD_AROUND);
//        } else {
//            messages = conversationViewModel.getMessages(conversation, channelPrivateChatUser);
//        }
        if(messageViewModel == null){
            MessageViewModel.Factory factory = new MessageViewModel.Factory(
                    this.getActivity().getApplication(), 0);

            messageViewModel = new ViewModelProvider(this, factory)
                    .get(MessageViewModel.class);
        }
        long searchId = -1;
        if(conversationId == 0){
            adapter.setMessageList(null);
        }else{
            searchId = conversationId;
        }
        Log.i("Junwang", "searchId="+searchId);
        messages = messageViewModel.getMessages(searchId);
        messages.observe(getViewLifecycleOwner(), uiMessages -> {
            if(uiMessages != null && uiMessages.size() > 0){
                swipeRefreshLayout.setRefreshing(false);
                Log.i("Junwang", "update messages size="+uiMessages.size());
                adapter.setMessageList(uiMessages);
//            if(isFromSearch){
                adapter.notifyDataSetChanged();
//                isFromSearch = false;
//            }
                if (adapter.getItemCount() > 1){
                    if (initialFocusedMessageId == -1){
                        moveToBottom = true;
                        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                    }else{
                        int initialMessagePosition = adapter.getMessagePosition(focusMessageId);
                        Log.i("Junwang", "focusMessageId="+focusMessageId+", initialMessagePosition="+initialMessagePosition);
                        if (initialMessagePosition != -1) {
                            recyclerView.scrollToPosition(initialMessagePosition);
                            adapter.highlightFocusMessage(initialMessagePosition);
                        }
                    }
                }
                if(uiMessages != null && uiMessages.size() > 0) {
//                    ((ChannelConversationActivity) getActivity()).updateConversationLastMsgId(uiMessages.get(uiMessages.size() - 1).getId());
                }
            }
        });
    }


    private void setTitle() {
        if (!TextUtils.isEmpty(conversationTitle)) {
            setActivityTitle(conversationTitle);
        }

        if(userViewModel == null ){
            UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                    this.getActivity().getApplication(), null);
            userViewModel = new ViewModelProvider(this, factory)
                    .get(UserInfoViewModel.class);
        }
        userViewModel.getUserInfo(chatbotId).observe(getViewLifecycleOwner(), userInfo -> {
            Log.i("Junwang", "query end");
            if(userInfo != null){
                Log.i("Junwang", "query end userinfo != null");
                setActivityTitle(userInfo.getName());
                setSwitchButton(userInfo.getMenu());
            }
        });
    }

    private void setActivityTitle(String title) {
        Activity activity = getActivity();
        if (activity != null) {
            activity.setTitle(title);
        }
    }

    @OnTouch({R2.id.contentLayout, R2.id.msgRecyclerView})
    boolean onTouch(View view, MotionEvent event) {
//        if (event.getAction() == MotionEvent.ACTION_DOWN && inputPanel.extension.canHideOnScroll()) {
//            inputPanel.collapse();
//        }
        Log.i("Junwang", "inputPanel.closeConversationInputPanel");
        inputPanel.closeConversationInputPanel();
        if(mThreeButtonMenu != null){
            mThreeButtonMenu.closeMenu();
        }else if(mTwoButtonMenu != null){
            mTwoButtonMenu.closeMenu();
        }
        return false;
    }

    @Override
    public void onPortraitClick(UserInfo userInfo) {
        Log.i("Junwang", "conversationFragment onPortraitClick");
        ChatbotIntroduceActivity.start(getContext(), chatbotId, null);
    }

    @Override
    public void onPortraitLongClick(UserInfo userInfo) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode >= ConversationExtension.REQUEST_CODE_MIN) {
//            boolean result = inputPanel.extension.onActivityResult(requestCode, resultCode, data);
//            if (result) {
//                return;
//            }
//            Log.d(TAG, "extension can not handle " + requestCode);
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        inputPanel.onActivityPause();
//        messageViewModel.stopPlayAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conversationId == 0) {
            return;
        }

        if(msgLiveData != null){
            msgLiveData.removeObserver(messageLiveDataObserver);
        }
        if(msgUpdateLiveData != null){
            msgUpdateLiveData.removeObserver(messageUpdateLiveDatObserver);
        }
        if(msgRemovedLiveData != null){
            msgRemovedLiveData.removeObserver(messageRemovedLiveDataObserver);
        }
        inputPanel.onDestroy();
    }

    boolean onBackPressed() {
        boolean consumed = true;
        if (rootLinearLayout.getCurrentInput() != null) {
            rootLinearLayout.hideAttachedInput(true);
            inputPanel.closeConversationInputPanel();
        } else if (multiMessageActionContainerLinearLayout.getVisibility() == View.VISIBLE) {
            toggleConversationMode();
        } else {
            consumed = false;
        }
//        Editable content = inputPanel.editText.getText();
//        if (TextUtils.isEmpty(content)) {
//            if((conversation != null) && (conversation.getDraftSnippetText() != null)){
//                conversation.setDraftSnippetText(null);
//                ((ConversationActivity)(getActivity())).updateDraft(null);
//            }
//            return false;
//        }
//        if((conversation != null) && (conversation.getDraftSnippetText() != null)){
//            ((ConversationActivity)(getActivity())).updateDraft(content.toString().trim());
//        }
//        else if(conversation != null){
//            ((ConversationActivity)(getActivity())).saveDraft(getContext(), content.toString().trim(), conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationUUID(), false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
//        }else if(chatbotId != null){
//            ((ConversationActivity)(getActivity())).saveDraft(getContext(), content.toString().trim(), null, chatbotId, null,false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
//        }
        return consumed;
    }

    @Override
    public void onKeyboardShown() {
        inputPanel.onKeyboardShown();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onKeyboardHidden() {
        inputPanel.onKeyboardHidden();
    }

    private void reloadMessage() {
        messageViewModel.getMessages(conversationId).observe(getViewLifecycleOwner(), messages -> {
            adapter.setMessages(messages);
            Log.i("Junwang", "reloadMessage setMessages");
            adapter.notifyDataSetChanged();
        });
    }

    private void loadMoreOldMessages() {
        Log.i("Junwang", "loadMoreOldMessages");
        long fromMessageId = Long.MAX_VALUE;
//        long fromMessageUid = Long.MAX_VALUE;
        if (adapter.getMessages() != null && !adapter.getMessages().isEmpty()) {
            fromMessageId = adapter.getItem(0).getId();
//            fromMessageUid = adapter.getItem(0).message.messageUid;
        }

        messageViewModel.loadOldMessages(conversationId, fromMessageId, MESSAGE_LOAD_COUNT_PER_TIME)
                .observe(getViewLifecycleOwner(), uiMessages -> {
                    adapter.addMessagesAtHead(uiMessages);

                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void loadMoreNewMessages() {
        loadingNewMessage = true;
        adapter.showLoadingNewMessageProgressBar();
        messageViewModel.loadNewMessages(conversationId, adapter.getItem(adapter.getItemCount() - 2).getId(), MESSAGE_LOAD_COUNT_PER_TIME)
                .observe(this, messages -> {
                    loadingNewMessage = false;
                    adapter.dismissLoadingNewMessageProgressBar();

                    if (messages == null || messages.isEmpty()) {
                        shouldContinueLoadNewMessage = false;
                    }
                    if (messages != null && !messages.isEmpty()) {
                        adapter.addMessagesAtTail(messages);
                    }
                });
    }

    private Runnable resetConversationTitleRunnable = this::resetConversationTitle;

    private void resetConversationTitle() {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        if (!TextUtils.equals(conversationTitle, getActivity().getTitle())) {
            setActivityTitle(conversationTitle);
            handler.removeCallbacks(resetConversationTitleRunnable);
        }
    }

    @Override
    public void onInputPanelExpanded() {
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
    }

    @Override
    public void onInputPanelCollapsed() {
        // do nothing
    }

    public void toggleMultiMessageMode(Message message) {
//        inputPanel.setVisibility(View.GONE);
        inputPanelFrameLayout.setVisibility(View.GONE);
//        message.isChecked = true;
        adapter.setMode(ConversationMessageAdapter.MODE_CHECKABLE);
        adapter.notifyDataSetChanged();
        multiMessageActionContainerLinearLayout.setVisibility(View.VISIBLE);
        setupMultiMessageAction();
    }

    public ChannelConversationMessageAdapter getAdapter(){
        return adapter;
    }

    public void toggleConversationMode() {
//        inputPanel.setVisibility(View.VISIBLE);
        inputPanelFrameLayout.setVisibility(View.VISIBLE);
        multiMessageActionContainerLinearLayout.setVisibility(View.GONE);
        adapter.setMode(ConversationMessageAdapter.MODE_NORMAL);
        adapter.clearMessageCheckStatus();
        adapter.notifyDataSetChanged();
    }

    public void setInputText(String text) {
        inputPanel.setInputText(text);
    }

    private void setupMultiMessageAction() {
        multiMessageActionContainerLinearLayout.removeAllViews();
        int width = getResources().getDisplayMetrics().widthPixels;

//        mAppExecutors.diskIO().execute(() -> {
//            ConversationListViewModel mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);
//            mViewModel.getConversationByChatbotId(chatbotId).observe(getViewLifecycleOwner(), conversationEntity -> {
//
//            });
//        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ConversationListViewModel mViewModel = new ViewModelProvider(ChannelConversationFragment.this).get(ConversationListViewModel.class);
                mViewModel.getConversationByChatbotId(chatbotId).observe(getViewLifecycleOwner(), conversationEntity -> {
                    List<MultiMessageAction> actions = MultiMessageActionManager.getInstance().getConversationActions(conversationEntity);
                    for (MultiMessageAction action : actions) {
                        action.onBind(ChannelConversationFragment.this, conversationEntity);
                        ImageView imageView = new ImageView(getActivity());
                        imageView.setImageResource(action.iconResId());


                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width / actions.size(), LinearLayout.LayoutParams.WRAP_CONTENT);
                        multiMessageActionContainerLinearLayout.addView(imageView, layoutParams);
                        ViewGroup.LayoutParams p = imageView.getLayoutParams();
                        p.height = 70;
                        imageView.requestLayout();

                        imageView.setOnClickListener(v -> {
                            List<UiMessage> checkedMessages = adapter.getCheckedMessages();
                            if (action.confirm()) {
                                new MaterialDialog.Builder(getActivity()).content(action.confirmPrompt())
                                        .negativeText("取消")
                                        .positiveText("确认")
                                        .onPositive((dialog, which) -> {
                                            action.onClick(checkedMessages);
                                            toggleConversationMode();
                                        })
                                        .build()
                                        .show();

                            } else {
                                action.onClick(checkedMessages);
                                toggleConversationMode();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public void onMessageCheck(Message message, boolean checked) {
    }
}
