package com.stv.msgservice.ui.conversation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
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
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMenuEntity;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.conversation.ext.core.ConversationExtension;
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

public class ConversationFragment extends Fragment implements
        KeyboardAwareLinearLayout.OnKeyboardShownListener,
        KeyboardAwareLinearLayout.OnKeyboardHiddenListener,
        ConversationMessageAdapter.OnPortraitClickListener,
        ConversationMessageAdapter.OnPortraitLongClickListener,
        ConversationInputPanel.OnConversationInputPanelStateChangeListener,
        ConversationMessageAdapter.OnMessageCheckListener {

    private static final String TAG = "convFragment";

    public static final int REQUEST_PICK_MENTION_CONTACT = 100;
    public static final int REQUEST_CODE_GROUP_VIDEO_CHAT = 101;
    public static final int REQUEST_CODE_GROUP_AUDIO_CHAT = 102;

    public Conversation conversation;
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
    ConversationInputPanel inputPanel;



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


//    @BindView(R2.id.unreadCountLinearLayout)
//    LinearLayout unreadCountLinearLayout;
//    @BindView(R2.id.unreadCountTextView)
//    TextView unreadCountTextView;
//    @BindView(R2.id.unreadMentionCountTextView)
//    TextView unreadMentionCountTextView;

    public ConversationMessageAdapter adapter;
    private boolean moveToBottom = true;
//    private ConversationViewModel conversationViewModel;
//    private SettingViewModel settingViewModel;
    private MessageViewModel messageViewModel;
    private UserInfoViewModel userViewModel;
//    private GroupViewModel groupViewModel;
//    private ChatRoomViewModel chatRoomViewModel;

    private Handler handler;
    private long initialFocusedMessageId;
    private long firstUnreadMessageId;
    // ??????channel???????????????????????????????????????
    private String channelPrivateChatUser;
    private String conversationTitle = "";
    private boolean isFromSearch;
    private LinearLayoutManager layoutManager;
    private String chatbotId;

    private Observer<MessageEntity> messageLiveDataObserver = new Observer<MessageEntity>() {
        @Override
        public void onChanged(@Nullable MessageEntity uiMessage) {
//            if (!isMessageInCurrentConversation(uiMessage)) {
//                return;
//            }
//            MessageContent content = uiMessage.message.content;
            if (/*isDisplayableMessage(uiMessage)*/true) {
                // ?????????????????????????????????????????????????????????????????????????????????????????????
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
//                if (moveToBottom/* || uiMessage.message.sender.equals(ChatManager.Instance().getUserId())*/) {
//                    UIUtils.postTaskDelay(() -> {
//
//                                int position = adapter.getItemCount() - 1;
//                                if (position < 0) {
//                                    return;
//                                }
//                                recyclerView.scrollToPosition(position);
//                            },
//                            100);
//                }
            }
//            if (content instanceof TypingMessageContent && uiMessage.message.direction == MessageDirection.Receive) {
//                updateTypingStatusTitle((TypingMessageContent) content);
//            } else {
//                resetConversationTitle();
//            }
            resetConversationTitle();

//            if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED && uiMessage.message.direction == MessageDirection.Receive) {
//                conversationViewModel.clearUnreadStatus(conversation);
//            }
//            conversationViewModel.clearUnreadStatus(conversation);
            if(conversation != null){
                messageViewModel.clearUnreadStatus(conversation.getId());
            }
        }
    };
    private Observer<MessageEntity> messageUpdateLiveDatObserver = new Observer<MessageEntity>() {
        @Override
        public void onChanged(@Nullable MessageEntity uiMessage) {
//            if (!isMessageInCurrentConversation(uiMessage)) {
//                return;
//            }
//            if (isDisplayableMessage(uiMessage)) {
//                adapter.updateMessage(uiMessage);
//            }
            Log.i("Junwang", "messageUpdateLiveDatObserver onChanged");
            if(conversation == null){
                adapter.updateMessage(uiMessage);
            }
            ((ConversationActivity)getActivity()).updateMesasge(uiMessage);
        }
    };

    private Observer<Message> messageRemovedLiveDataObserver = new Observer<Message>() {
        @Override
        public void onChanged(@Nullable Message uiMessage) {
//            // ?????????server api????????????????????????????????????uid
//            if (uiMessage != null && !isMessageInCurrentConversation(uiMessage)) {
//                return;
//            }
//            if (uiMessage.getId() == 0 || isDisplayableMessage(uiMessage)) {
//                adapter.removeMessage(uiMessage);
//            }
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

    private Observer<Conversation> clearConversationMessageObserver = new Observer<Conversation>() {
        @Override
        public void onChanged(Conversation conversation) {
            if (conversation.equals(ConversationFragment.this.conversation)) {
                adapter.setMessages(null);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private Observer<List<UserInfo>> userInfoUpdateLiveDataObserver = new Observer<List<UserInfo>>() {
        @Override
        public void onChanged(@Nullable List<UserInfo> userInfos) {
            if (conversation == null) {
                return;
            }
//            if (conversation.type == Conversation.ConversationType.Single) {
//                conversationTitle = null;
//                setTitle();
//            }
            conversationTitle = null;
            setTitle();

            int start = layoutManager.findFirstVisibleItemPosition();
            int end = layoutManager.findLastVisibleItemPosition();
            adapter.notifyItemRangeChanged(start, end - start + 1, userInfos);
        }
    };

//    private void initGroupObservers() {
//        groupMembersUpdateLiveDataObserver = groupMembers -> {
//            if (groupMembers == null || groupInfo == null) {
//                return;
//            }
//            for (GroupMember member : groupMembers) {
//                if (member.groupId.equals(groupInfo.target) && member.memberId.equals(userViewModel.getUserId())) {
//                    groupMember = member;
//                    updateGroupMuteStatus();
//                    break;
//                }
//            }
//        };
//
//        groupInfosUpdateLiveDataObserver = groupInfos -> {
//            if (groupInfo == null || groupInfos == null) {
//                return;
//            }
//            for (GroupInfo info : groupInfos) {
//                if (info.target.equals(groupInfo.target)) {
//                    groupInfo = info;
//                    updateGroupMuteStatus();
//                    setTitle();
//                    adapter.notifyDataSetChanged();
//                    break;
//                }
//            }
//        };
//
//
//        groupViewModel.groupInfoUpdateLiveData().observeForever(groupInfosUpdateLiveDataObserver);
//        groupViewModel.groupMembersUpdateLiveData().observeForever(groupMembersUpdateLiveDataObserver);
//    }

//    private void unInitGroupObservers() {
//        if (groupViewModel == null) {
//            return;
//        }
//        groupViewModel.groupInfoUpdateLiveData().removeObserver(groupInfosUpdateLiveDataObserver);
//        groupViewModel.groupMembersUpdateLiveData().removeObserver(groupMembersUpdateLiveDataObserver);
//    }

    private boolean isMessageInCurrentConversation(Message message) {
        if (conversation == null || message == null) {
            return false;
        }
//        return conversation.equals(message.message.conversation);
        return conversation.getId() == message.getConversationId();
    }

    public ConversationInputPanel getConversationInputPanel() {
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
        View view = inflater.inflate(R.layout.conversation_activity, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (messageViewModel != null && conversation != null) {
//            conversationViewModel.clearUnreadStatus(conversation);
            messageViewModel.clearUnreadStatus(conversation.getId());
        }
    }

    public void setupConversation(Conversation conversation, String chatbotId, String title, long focusMessageId, String target, boolean isFromSearch) {
        this.conversation = conversation;
        this.chatbotId = chatbotId;
        this.conversationTitle = title;
        this.initialFocusedMessageId = focusMessageId;
        this.channelPrivateChatUser = target;
        this.isFromSearch = isFromSearch;
//        if(isFromSearch){
//            setupConversation(conversation);
//        }
//        setupConversation(conversation);
    }

//    @OnClick(R2.id.two_button_menu_container)
//    public void twoButtonMenuOnClick(){
//        Log.i("junwang", "mTwoBtnSwitchButton onClicked.");
//        startMenuSwitchAnimation(mTwoButtonMenu, inputPanel);
//    }
//
//    @OnClick(R2.id.three_button_menu_container)
//    public void threeButtonMenuOnClick(){
//        Log.i("junwang", "mTwoBtnSwitchButton onClicked.");
//        startMenuSwitchAnimation(mThreeButtonMenu, inputPanel);
//    }

//    public void switchInputPanel(){
//        Log.i("junwang", "switch button onClicked.");
//        Log.i("Junwang", "menuNumber=" + menuNumber);
//        if (menuNumber == 2) {
//            startMenuSwitchAnimation(inputPanel, mTwoButtonMenu);
//        } else if (menuNumber == 3) {
//            Log.i("Junwang", "start switch from compose to menu");
//            startMenuSwitchAnimation(inputPanel, mThreeButtonMenu);
//        }
//    }

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
        //add by junwang for chatbot menu start
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
        adapter = new ConversationMessageAdapter(this);
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
                // ??????????????????????????????????????????????????????????????????, ????????????????????????????????????true
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
        setupConversation(conversation);

//        settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
//        conversationViewModel = WfcUIKit.getAppScopeViewModel(ConversationViewModel.class);
//        conversationViewModel.clearConversationMessageLiveData().observeForever(clearConversationMessageObserver);
//        messageViewModel = ViewModelProviders.of(this).get(MessageViewModel.class);
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

//        messageViewModel.messageDeliverLiveData().observe(getActivity(), stringLongMap -> {
//            if (conversation == null) {
//                return;
//            }
//            Map<String, Long> deliveries = ChatManager.Instance().getMessageDelivery(conversation);
//            adapter.setDeliveries(deliveries);
//        });

//        messageViewModel.messageReadLiveData().observe(getActivity(), readEntries -> {
//            if (conversation == null) {
//                return;
//            }
//            Map<String, Long> convReadEntities = ChatManager.Instance().getConversationRead(conversation);
//            adapter.setReadEntries(convReadEntities);
//        });

//        userViewModel = ViewModelProviders.of(this).get(UserInfoViewModel.class);
        if(userViewModel == null ){
            UserInfoViewModel.Factory factory1 = new UserInfoViewModel.Factory(
                    this.getActivity().getApplication(), null);
            userViewModel = new ViewModelProvider(this, factory1)
                    .get(UserInfoViewModel.class);
        }

        userViewModel.userInfoLiveData().observeForever(userInfoUpdateLiveDataObserver);

//        settingUpdateLiveDataObserver = o -> {
//            if (groupInfo == null) {
//                return;
//            }
//            boolean show = "1".equals(userViewModel.getUserSetting(UserSettingScope.GroupHideNickname, groupInfo.target));
//            if (showGroupMemberName != show) {
//                showGroupMemberName = show;
//                adapter.notifyDataSetChanged();
//            }
//            reloadMessage();
//        };
//        settingViewModel.settingUpdatedLiveData().observeForever(settingUpdateLiveDataObserver);
    }

    private void setupConversation(Conversation conversation) {

//        if (conversation.type == Conversation.ConversationType.Group) {
//            groupViewModel = ViewModelProviders.of(this).get(GroupViewModel.class);
//            initGroupObservers();
//            groupViewModel.getGroupMembers(conversation.target, true);
//            groupInfo = groupViewModel.getGroupInfo(conversation.target, true);
//            groupMember = groupViewModel.getGroupMember(conversation.target, userViewModel.getUserId());
//            showGroupMemberName = "1".equals(userViewModel.getUserSetting(UserSettingScope.GroupHideNickname, groupInfo.target));
//
//            updateGroupMuteStatus();
//        }
//        userViewModel.getUserInfo(userViewModel.getUserId(), true);

        inputPanel.setupConversation(conversation, chatbotId);

//        if (conversation.type != Conversation.ConversationType.ChatRoom) {
//            loadMessage(initialFocusedMessageId);
//        } else {
//            joinChatRoom();
//        }
        loadMessage(initialFocusedMessageId);

//        ConversationInfo conversationInfo = ChatManager.Instance().getConversation(conversation);
//        int unreadCount = conversationInfo.unreadCount.unread + conversationInfo.unreadCount.unreadMention + conversationInfo.unreadCount.unreadMentionAll;
//        if (unreadCount > 10 && unreadCount < 300) {
//            firstUnreadMessageId = ChatManager.Instance().getFirstUnreadMessageId(conversation);
//            showUnreadMessageCountLabel(unreadCount);
//        }
//        conversationViewModel.clearUnreadStatus(conversation);

        setTitle();
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
        if(conversation == null){
            adapter.setMessageList(null);
        }else{
            searchId = conversation.getId();
        }
        Log.i("Junwang", "searchId="+searchId);
        messages = messageViewModel.getMessages(searchId);
//        messages = messageViewModel.loadOldMessages(conversation.getId(), conversation.getLatestMessageId()+10000, 20);

        // load message
//        swipeRefreshLayout.setRefreshing(true);
//        adapter.setDeliveries(ChatManager.Instance().getMessageDelivery(conversation));
//        adapter.setReadEntries(ChatManager.Instance().getConversationRead(conversation));
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
                    ((ConversationActivity) getActivity()).updateConversationLastMsgId(uiMessages.get(uiMessages.size() - 1).getId());
                }
            }

//            adapter.setMessages(uiMessages);
//            adapter.notifyDataSetChanged();
//            if (adapter.getItemCount() > 1) {
//                int initialMessagePosition;
//                if (focusMessageId != -1) {
//                    initialMessagePosition = adapter.getMessagePosition(focusMessageId);
//                    if (initialMessagePosition != -1) {
//                        recyclerView.scrollToPosition(initialMessagePosition);
//                        adapter.highlightFocusMessage(initialMessagePosition);
//                    }
//                } else {
//                    moveToBottom = true;
//                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
//                }
//            }
        });
    }

    private void updateGroupMuteStatus() {
//        if (groupInfo == null || groupMember == null) {
//            return;
//        }
//        if (groupInfo.mute == 1) {
//            if (groupMember.type != GroupMember.GroupMemberType.Owner && groupMember.type != GroupMember.GroupMemberType.Manager) {
//                inputPanel.disableInput("???????????????");
//            }
//        } else {
//            inputPanel.enableInput();
//        }
    }

//    @OnClick(R2.id.unreadCountTextView)
//    void onUnreadCountTextViewClick() {
//        hideUnreadMessageCountLabel();
//        shouldContinueLoadNewMessage = true;
//        loadMessage(firstUnreadMessageId);
//    }

    private void showUnreadMessageCountLabel(int count) {
//        unreadCountLinearLayout.setVisibility(View.VISIBLE);
//        unreadCountTextView.setVisibility(View.VISIBLE);
//        unreadCountTextView.setText(count + "?????????");
    }

    private void hideUnreadMessageCountLabel() {
//        unreadCountTextView.setVisibility(View.GONE);
    }

    private void showUnreadMentionCountLabel(int count) {
//        unreadCountLinearLayout.setVisibility(View.VISIBLE);
//        unreadMentionCountTextView.setVisibility(View.VISIBLE);
//        unreadMentionCountTextView.setText(count + "???@??????");
    }

    private void hideUnreadMentionCountLabel() {
//        unreadMentionCountTextView.setVisibility(View.GONE);
    }

    private void joinChatRoom() {
//        chatRoomViewModel = ViewModelProviders.of(this).get(ChatRoomViewModel.class);
//        chatRoomViewModel.joinChatRoom(conversation.target)
//                .observe(this, new Observer<OperateResult<Boolean>>() {
//                    @Override
//                    public void onChanged(@Nullable OperateResult<Boolean> booleanOperateResult) {
//                        if (booleanOperateResult.isSuccess()) {
//                            String welcome = "?????? %s ???????????????";
//                            TipNotificationContent content = new TipNotificationContent();
//                            String userId = userViewModel.getUserId();
//                            UserInfo userInfo = userViewModel.getUserInfo(userId, false);
//                            if (userInfo != null) {
//                                content.tip = String.format(welcome, userViewModel.getUserDisplayName(userInfo));
//                            } else {
//                                content.tip = String.format(welcome, "<" + userId + ">");
//                            }
//                            handler.postDelayed(() -> {
//                                messageViewModel.sendMessage(conversation, content);
//                            }, 1000);
//                            setChatRoomConversationTitle();
//
//                        } else {
//                            Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
//                            getActivity().finish();
//                        }
//                    }
//                });
    }

    private void quitChatRoom() {
//        String welcome = "%s ??????????????????";
//        TipNotificationContent content = new TipNotificationContent();
//        String userId = userViewModel.getUserId();
//        UserInfo userInfo = userViewModel.getUserInfo(userId, false);
//        if (userInfo != null) {
//            content.tip = String.format(welcome, userViewModel.getUserDisplayName(userInfo));
//        } else {
//            content.tip = String.format(welcome, "<" + userId + ">");
//        }
//        messageViewModel.sendMessage(conversation, content);
//        chatRoomViewModel.quitChatRoom(conversation.target);
    }

    private void setChatRoomConversationTitle() {
//        chatRoomViewModel.getChatRoomInfo(conversation.target, System.currentTimeMillis())
//                .observe(this, chatRoomInfoOperateResult -> {
//                    if (chatRoomInfoOperateResult.isSuccess()) {
//                        ChatRoomInfo chatRoomInfo = chatRoomInfoOperateResult.getResult();
//                        conversationTitle = chatRoomInfo.title;
//                        setActivityTitle(conversationTitle);
//                    }
//                });
    }

    private void setTitle() {
        if (!TextUtils.isEmpty(conversationTitle)) {
            setActivityTitle(conversationTitle);
        }

//        if (conversation.type == Conversation.ConversationType.Single) {
//            UserInfo userInfo = ChatManagerHolder.gChatManager.getUserInfo(conversation.target, false);
//            conversationTitle = userViewModel.getUserDisplayName(userInfo);
//        } else if (conversation.type == Conversation.ConversationType.Group) {
//            if (groupInfo != null) {
//                conversationTitle = groupInfo.name + "(" + groupInfo.memberCount + "???)";
//            }
//        } else if (conversation.type == Conversation.ConversationType.Channel) {
//            ChannelViewModel channelViewModel = ViewModelProviders.of(this).get(ChannelViewModel.class);
//            ChannelInfo channelInfo = channelViewModel.getChannelInfo(conversation.target, false);
//            if (channelInfo != null) {
//                conversationTitle = channelInfo.name;
//            }
//
//            if (!TextUtils.isEmpty(channelPrivateChatUser)) {
//                UserInfo channelPrivateChatUserInfo = userViewModel.getUserInfo(channelPrivateChatUser, false);
//                if (channelPrivateChatUserInfo != null) {
//                    conversationTitle += "@" + userViewModel.getUserDisplayName(channelPrivateChatUserInfo);
//                } else {
//                    conversationTitle += "@<" + channelPrivateChatUser + ">";
//                }
//            }
//        }
//        setActivityTitle(conversationTitle);
        if(userViewModel == null ){
            UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                    this.getActivity().getApplication(), null);
            userViewModel = new ViewModelProvider(this, factory)
                    .get(UserInfoViewModel.class);
        }
        String botId = null;
        if(conversation != null){
            botId = conversation.getSenderAddress();
        }else{
            botId = chatbotId;
        }
        userViewModel.getUserInfo(botId).observe(getViewLifecycleOwner(), userInfo -> {
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
        ChatbotIntroduceActivity.start(getContext(), conversation.getSenderAddress(), null);
//        if (groupInfo != null && groupInfo.privateChat == 1) {
//            boolean allowPrivateChat = false;
//            GroupMember groupMember = groupViewModel.getGroupMember(groupInfo.target, userViewModel.getUserId());
//            if (groupMember != null && groupMember.type == GroupMember.GroupMemberType.Normal) {
//                GroupMember targetGroupMember = groupViewModel.getGroupMember(groupInfo.target, userInfo.uid);
//                if (targetGroupMember != null && (targetGroupMember.type == GroupMember.GroupMemberType.Owner || targetGroupMember.type == GroupMember.GroupMemberType.Manager)) {
//                    allowPrivateChat = true;
//                }
//            } else if (groupMember != null && (groupMember.type == GroupMember.GroupMemberType.Owner || groupMember.type == GroupMember.GroupMemberType.Manager)) {
//                allowPrivateChat = true;
//            }
//
//            if (!allowPrivateChat) {
//                Toast.makeText(getActivity(), "?????????????????????", Toast.LENGTH_SHORT).show();
//                return;
//            }
//        }
//        if (userInfo.deleted == 0) {
//            Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//            intent.putExtra("userInfo", userInfo);
//            if(conversation.type == Conversation.ConversationType.Group) {
//                intent.putExtra("groupId", conversation.target);
//            }
//            startActivity(intent);
//        }
    }

    @Override
    public void onPortraitLongClick(UserInfo userInfo) {
//        if (conversation.type == Conversation.ConversationType.Group) {
//            SpannableString spannableString = mentionSpannable(userInfo);
//            int position = inputPanel.editText.getSelectionEnd();
//            inputPanel.editText.getEditableText().append(" ");
//            inputPanel.editText.getEditableText().replace(position, position + 1, spannableString);
//        } else {
//            inputPanel.editText.getEditableText().append(userViewModel.getUserDisplayName(userInfo));
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode >= ConversationExtension.REQUEST_CODE_MIN) {
            boolean result = inputPanel.extension.onActivityResult(requestCode, resultCode, data);
            if (result) {
                return;
            }
            Log.d(TAG, "extension can not handle " + requestCode);
        }
        if (resultCode == Activity.RESULT_OK) {
            /*if (requestCode == REQUEST_PICK_MENTION_CONTACT) {
                boolean isMentionAll = data.getBooleanExtra("mentionAll", false);
                SpannableString spannableString;
                if (isMentionAll) {
                    spannableString = mentionAllSpannable();
                } else {
                    String userId = data.getStringExtra("userId");
                    UserInfo userInfo = userViewModel.getUserInfo(userId, false);
                    spannableString = mentionSpannable(userInfo);
                }
                int position = inputPanel.editText.getSelectionEnd();
                position = position > 0 ? position - 1 : 0;
                inputPanel.editText.getEditableText().replace(position, position + 1, spannableString);

            } else*/ if (requestCode == REQUEST_CODE_GROUP_AUDIO_CHAT || requestCode == REQUEST_CODE_GROUP_VIDEO_CHAT) {
                onPickGroupMemberToVoipChat(data, requestCode == REQUEST_CODE_GROUP_AUDIO_CHAT);
            }
        }
    }

//    private SpannableString mentionAllSpannable() {
//        String text = "@????????? ";
//        SpannableString spannableString = new SpannableString(text);
//        spannableString.setSpan(new MentionSpan(true), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }
//
//    private SpannableString mentionSpannable(UserInfo userInfo) {
//        String text = "@" + userInfo.displayName + " ";
//        SpannableString spannableString = new SpannableString(text);
//        spannableString.setSpan(new MentionSpan(userInfo.uid), 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
//        return spannableString;
//    }

    @Override
    public void onPause() {
        super.onPause();
        inputPanel.onActivityPause();
//        messageViewModel.stopPlayAudio();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (conversation == null) {
            return;
        }

//        if (conversation.type == Conversation.ConversationType.ChatRoom) {
//            quitChatRoom();
//        }
        if(msgLiveData != null){
            msgLiveData.removeObserver(messageLiveDataObserver);
        }
        if(msgUpdateLiveData != null){
            msgUpdateLiveData.removeObserver(messageUpdateLiveDatObserver);
        }
        if(msgRemovedLiveData != null){
            msgRemovedLiveData.removeObserver(messageRemovedLiveDataObserver);
        }

//        messageViewModel.mediaUpdateLiveData().removeObserver(mediaUploadedLiveDataObserver);
//        userViewModel.userInfoLiveData().removeObserver(userInfoUpdateLiveDataObserver);

//        conversationViewModel.clearConversationMessageLiveData().removeObserver(clearConversationMessageObserver);
//        settingViewModel.settingUpdatedLiveData().removeObserver(settingUpdateLiveDataObserver);

//        unInitGroupObservers();
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
        Editable content = inputPanel.editText.getText();
        if (TextUtils.isEmpty(content)) {
            if((conversation != null) && (conversation.getDraftSnippetText() != null)){
                conversation.setDraftSnippetText(null);
                ((ConversationActivity)(getActivity())).updateDraft(null);
            }
            return false;
        }
//        messageViewModel.searchMessages(content.toString().trim()).observe(fragment, messageEntityList -> {
//            if(messageEntityList != null && messageEntityList.size() > 0){
//                for(int i=0; i<messageEntityList.size(); i++){
//                    Log.i("Junwang", "matched messageid="+messageEntityList.get(i).getId()+", content="+messageEntityList.get(i).getContent());
//                }
//            }
//        });
        if((conversation != null) && (conversation.getDraftSnippetText() != null)){
            ((ConversationActivity)(getActivity())).updateDraft(content.toString().trim());
        }
        else if(conversation != null){
            ((ConversationActivity)(getActivity())).saveDraft(getContext(), content.toString().trim(), conversation.getDestinationAddress(), conversation.getSenderAddress(), conversation.getConversationUUID(), false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
        }else if(chatbotId != null){
            ((ConversationActivity)(getActivity())).saveDraft(getContext(), content.toString().trim(), null, chatbotId, null,false, null, null, MessageConstants.CONTENT_TYPE_TEXT, null);
        }
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
//        conversationViewModel.getMessages(conversation, channelPrivateChatUser).observe(this, uiMessages -> {
//            adapter.setMessages(uiMessages);
//            adapter.notifyDataSetChanged();
//        });
        messageViewModel.getMessages(conversation.getId()).observe(getViewLifecycleOwner(), messages -> {
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

        messageViewModel.loadOldMessages(conversation.getId(), fromMessageId, MESSAGE_LOAD_COUNT_PER_TIME)
                .observe(getViewLifecycleOwner(), uiMessages -> {
                    adapter.addMessagesAtHead(uiMessages);

                    swipeRefreshLayout.setRefreshing(false);
                });
    }

    private void loadMoreNewMessages() {
        loadingNewMessage = true;
        adapter.showLoadingNewMessageProgressBar();
        messageViewModel.loadNewMessages(conversation.getId(), adapter.getItem(adapter.getItemCount() - 2).getId(), MESSAGE_LOAD_COUNT_PER_TIME)
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

//    private void updateTypingStatusTitle(TypingMessageContent typingMessageContent) {
//        String typingDesc = "";
//        switch (typingMessageContent.getTypingType()) {
//            case TypingMessageContent.TYPING_TEXT:
//                typingDesc = "??????????????????";
//                break;
//            case TypingMessageContent.TYPING_VOICE:
//                typingDesc = "??????????????????";
//                break;
//            case TypingMessageContent.TYPING_CAMERA:
//                typingDesc = "??????????????????";
//                break;
//            case TypingMessageContent.TYPING_FILE:
//                typingDesc = "????????????????????????";
//                break;
//            case TypingMessageContent.TYPING_LOCATION:
//                typingDesc = "????????????????????????";
//                break;
//            default:
//                typingDesc = "unknown";
//                break;
//        }
//        setActivityTitle(typingDesc);
//        handler.postDelayed(resetConversationTitleRunnable, 5000);
//    }

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

    public ConversationMessageAdapter getAdapter(){
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
        List<MultiMessageAction> actions = MultiMessageActionManager.getInstance().getConversationActions(conversation);
        int width = getResources().getDisplayMetrics().widthPixels;

        for (MultiMessageAction action : actions) {
            action.onBind(this, conversation);
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
                            .negativeText("??????")
                            .positiveText("??????")
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
    }

    @Override
    public void onMessageCheck(Message message, boolean checked) {
//        List<Message> checkedMessages = adapter.getCheckedMessages();
//        setAllClickableChildViewState(multiMessageActionContainerLinearLayout, checkedMessages.size() > 0);
    }

    private void setAllClickableChildViewState(View view, boolean enable) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setAllClickableChildViewState(((ViewGroup) view).getChildAt(i), enable);
            }
        }
        if (view.isClickable()) {
            view.setEnabled(enable);
        }
    }

    public void pickGroupMemberToVoipChat(boolean isAudioOnly) {
//        Intent intent = new Intent(getActivity(), PickGroupMemberActivity.class);
//        GroupInfo groupInfo = groupViewModel.getGroupInfo(conversation.target, false);
//        intent.putExtra("groupInfo", groupInfo);
//        int maxCount = AVEngineKit.isSupportMultiCall() ? (isAudioOnly ? AVEngineKit.MAX_AUDIO_PARTICIPANT_COUNT - 1 : AVEngineKit.MAX_VIDEO_PARTICIPANT_COUNT - 1) : 1;
//        intent.putExtra("maxCount", maxCount);
//        startActivityForResult(intent, isAudioOnly ? REQUEST_CODE_GROUP_AUDIO_CHAT : REQUEST_CODE_GROUP_VIDEO_CHAT);
    }

    private void onPickGroupMemberToVoipChat(Intent intent, boolean isAudioOnly) {
//        List<String> memberIds = intent.getStringArrayListExtra(PickGroupMemberActivity.EXTRA_RESULT);
//        if (memberIds != null && memberIds.size() > 0) {
//            if (AVEngineKit.isSupportMultiCall()) {
//                WfcUIKit.multiCall(getActivity(), conversation.target, memberIds, isAudioOnly);
//            } else {
//                WfcUIKit.singleCall(getActivity(), memberIds.get(0), isAudioOnly);
//            }
//        }
    }
}
