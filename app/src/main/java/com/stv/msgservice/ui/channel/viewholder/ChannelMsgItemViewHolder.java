package com.stv.msgservice.ui.channel.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.ConversationContextMenuItem;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.third.utils.TimeUtils;
import com.stv.msgservice.ui.GlideApp;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.channel.activity.ChannelConversationActivity;
import com.stv.msgservice.ui.channel.activity.ChannelConversationFragment;
import com.stv.msgservice.ui.channel.activity.ChannelFavoritedMsgFragment;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.message.viewholder.MessageContextMenuItemTags;
import com.stv.msgservice.ui.conversationlist.viewholder.ConversationContextMenuItemTags;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class ChannelMsgItemViewHolder extends RecyclerView.ViewHolder {
    protected Fragment fragment;
    protected View itemView;
    protected Conversation conversationInfo;
    protected RecyclerView.Adapter adapter;
    protected ConversationListViewModel conversationListViewModel;
    protected MessageUserInfoEntity message;
    protected int position;
    ImageView addToFavMsg;
//    private MessageViewModel messageViewModel;

//    @BindView(R2.id.channel_chatbot_nameTextView)
    protected TextView channel_chatbot_nameTextView;
//    @BindView(R2.id.channel_chatbot_timeTextView)
    protected TextView channel_chatbot_timeTextView;
//    @BindView(R2.id.channel_chatbot_portraitImageView)
    protected ImageView channel_chatbot_portraitImageView;
    private MessageViewModel.Factory factory;
    private MessageViewModel messageViewModel;

    public ChannelMsgItemViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(itemView);
        this.fragment = fragment;
        this.itemView = itemView;
        this.adapter = adapter;

        channel_chatbot_portraitImageView = itemView.findViewById(R.id.channel_chatbot_portraitImageView);
        channel_chatbot_nameTextView = itemView.findViewById(R.id.channel_chatbot_nameTextView);
        channel_chatbot_timeTextView = itemView.findViewById(R.id.channel_chatbot_timeTextView);
        addToFavMsg = itemView.findViewById(R.id.channel_fav_image_button);
        factory = new MessageViewModel.Factory(
                fragment.getActivity().getApplication(), 0);
        messageViewModel = new ViewModelProvider(fragment, factory)
                .get(MessageViewModel.class);
//        ButterKnife.bind(this, itemView);
//        conversationListViewModel = ViewModelProviders
//                .of(fragment, new ConversationListViewModelFactory(Arrays.asList(Conversation.ConversationType.Single, Conversation.ConversationType.Group), Arrays.asList(0)))
//                .get(ConversationListViewModel.class);
//        conversationViewModel = ViewModelProviders.of(fragment).get(ConversationViewModel.class);
//        conversationListViewModel = new ViewModelProvider(fragment).get(ConversationListViewModel.class);
//        messageViewModel = new ViewModelProvider(fragment).get(MessageViewModel.class);
    }

//    final public void onBind(Conversation conversationInfo, int position) {
//        this.conversationInfo = conversationInfo;
//        onBind(conversationInfo);
//    }

    public void onBind(MessageUserInfoEntity message, int position) {
        this.message = message;
        this.position = position;

        if(message.getSenderAddress() != null){
            String name = message.getName();
            String portrait = message.getPortrait();
            GlideApp.with(fragment)
                    .load(portrait)
                    .placeholder(R.mipmap.avatar_def)
                    .transform(new CenterCrop(),new GlideCircleWithBorder())
//                        .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
                    .into(channel_chatbot_portraitImageView);
            channel_chatbot_nameTextView.setText(name);
            channel_chatbot_portraitImageView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(fragment.getContext(), ChannelConversationActivity.class);
                    intent.putExtra("conversationId", message.getConversationId());
                    intent.putExtra("chatbotId", message.getSenderAddress());
                    intent.putExtra("chatbotPortrait", portrait);
                    intent.putExtra("chatbotName", name);
                    intent.putExtra("chatbotIsAttentioned", message.isAttentioned());
                    intent.putExtra("destinationAddress", message.getDestinationAddress());
                    intent.putExtra("conversationUUID", message.getConversationUUID());
                    intent.putExtra("toFocusMessageId", -1);
                    intent.putExtra("fromSearch", false);
                    fragment.startActivity(intent);

//                    ConversationListViewModel mConversationListViewModel = new ViewModelProvider(fragment).get(ConversationListViewModel.class);
//                    LiveData<ConversationEntity> liveData = mConversationListViewModel.getConversationByChatbotId(message.getSenderAddress());
//                    liveData.observe(fragment, conversationEntity -> {
//                        if(conversationEntity != null) {
//                            Intent intent = new Intent(fragment.getContext(), ChannelConversationActivity.class);
//                            intent.putExtra("conversationId", message.getConversationId());
//                            intent.putExtra("conversation", conversationEntity);
//                            intent.putExtra("toFocusMessageId", -1);
//                            intent.putExtra("fromSearch", false);
//                            fragment.startActivity(intent);
//                        }else{
//                            Intent intent = new Intent(fragment.getContext(), ChannelConversationActivity.class);
//                            intent.putExtra("chatbotId", message.getSenderAddress());
//                            intent.putExtra("fromSearch", false);
//                            intent.putExtra("conversationTitle", message.getName());
//                            fragment.startActivity(intent);
//                        }
//                        liveData.removeObservers(fragment);
//                    });
                }
            });
            channel_chatbot_timeTextView.setText(TimeUtils.getMsgFormatTime(message.getReceivedTimeStamp()));
            itemView.setBackgroundResource(R.drawable.selector_common_item/*conversationInfo.isTop ? R.drawable.selector_stick_top_item : R.drawable.selector_common_item*/);
            if(fragment instanceof ChannelFavoritedMsgFragment){
                addToFavMsg.setImageResource(R.mipmap.channel_delete_msg);
            }else{
                if(message.getIsFavorited() == 0){
                    addToFavMsg.setImageResource(R.mipmap.channel_add_to_fav_msg);
                }else{
                    addToFavMsg.setImageResource(R.mipmap.channel_favorited_msg);
                }
                addToFavMsg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(message.getIsFavorited() == 0){
                            addToFavMsg.setImageResource(R.mipmap.channel_favorited_msg);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    messageViewModel.updateMessageFavoriteStatusById(message.getId(), 1, System.currentTimeMillis());
                                }
                            }).start();
                        }
                    }
                });
            }
        }

        try {
            onBind(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (message.isFocus) {
//            highlightItem(itemView, message);
//        }
    }

    protected abstract void onBind(MessageUserInfoEntity message);

    /**
     * 设置头像、名称
     *
//     * @param conversationInfo
     */
//    protected /*abstract*/ void onBindConversationInfo(Conversation conversationInfo){
//        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
//                fragment.getActivity().getApplication(), /*conversationInfo.getNormalizedDestination()*/conversationInfo.getSenderAddress());
//
//        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(fragment, factory)
//                .get(UserInfoViewModel.class);
//        Log.i("Junwang", "senderaddress="+conversationInfo.getSenderAddress());
//        userInfoViewModel.getUserInfo(/*conversationInfo.getNormalizedDestination()*/conversationInfo.getSenderAddress()).observe(fragment, userInfo -> {
//            if(userInfo != null){
//                String name = userInfo.getName();
//                String portrait = userInfo.getPortrait();
//                GlideApp.with(fragment)
//                        .load(portrait)
//                        .placeholder(R.mipmap.avatar_def)
//                        .transform(new CenterCrop(),new GlideCircleWithBorder())
////                        .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
//                        .into(channel_chatbot_portraitImageView);
//                channel_chatbot_nameTextView.setText(name);
//                channel_chatbot_portraitImageView.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        ChatbotIntroduceActivity.start(fragment.getContext(), userInfo.getUri(), null);
//                    }
//                });
//            }
//        });
//    }

//    protected abstract void sendTextMsg(String destination, String text);

//    public void onBind(Conversation conversationInfo) {
//        onBindConversationInfo(conversationInfo);
//
//        channel_chatbot_timeTextView.setText(TimeUtils.getMsgFormatTime(conversationInfo.getLastTimestamp()));
//        itemView.setBackgroundResource(R.drawable.selector_common_item/*conversationInfo.isTop ? R.drawable.selector_stick_top_item : R.drawable.selector_common_item*/);
//    }

//    public void onClick(View itemView) {
//        Intent intent = new Intent(fragment.getActivity(), ConversationActivity.class);
//        intent.putExtra("conversation", (ConversationEntity)conversationInfo);
//        fragment.startActivity(intent);
//    }

    @Optional
    @OnClick(R2.id.errorLinearLayout)
    public void onRetryClick(View itemView) {
        new MaterialDialog.Builder(fragment.getContext())
                .content("重新发送?")
                .negativeText("取消")
                .positiveText("重发")
                .onPositive((dialog, which) -> {
//                    ((ConversationActivity)(fragment.getActivity())).resendMsg((MessageEntity) message);
                })
                .build()
                .show();
    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_DELETE, confirm = false, priority = 11)
    public void removeMessage(View itemView, Message message) {
        new MaterialDialog.Builder(fragment.getContext())
                .items("删除消息")
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        if (position == 0) {
//                            messageViewModel.deleteMessage(message.getId());
//                        } else {
//                            messageViewModel.deleteRemoteMessage(message.message);
//                        }
//                        messageViewModel.deleteMessage(message.getId());
                        ((ConversationActivity)fragment.getActivity()).deleteMsg(message);
                    }
                })
                .show();
    }

    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_REMOVE,
            confirm = true,
            priority = 0)
    public void removeConversation(View itemView, Conversation conversationInfo) {
        ConversationEntity ce = new ConversationEntity();
        ce.setId(conversationInfo.getId());
        ((MainActivity)fragment.getActivity()).deleteConversation(ce);
//        conversationListViewModel.deleteConversation(ce);
    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_MULTI_CHECK, priority = 13)
    public void checkMessage(View itemView, Message message) {
        ((ChannelConversationFragment)fragment).toggleMultiMessageMode(message);
    }

    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_TOP, priority = 1)
    public void stickConversationTop(View itemView, Conversation conversationInfo) {
//        conversationListViewModel.setConversationTop(conversationInfo, true);
    }

    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_CANCEL_TOP, priority = 2)
    public void cancelStickConversationTop(View itemView, Conversation conversationInfo) {
//        conversationListViewModel.setConversationTop(conversationInfo, false);
    }

    /**
     * 长按触发的context menu的标题
     *
     * @param tag
     * @return
     */
    public String contextMenuTitle(Context context, String tag) {
        String title = "未设置";
        switch (tag) {
            case ConversationContextMenuItemTags.TAG_CLEAR:
                title = "清空会话";
                break;
            case ConversationContextMenuItemTags.TAG_REMOVE:
                title = "删除会话";
                break;
            case ConversationContextMenuItemTags.TAG_TOP:
                title = "置顶";
                break;
            case ConversationContextMenuItemTags.TAG_CANCEL_TOP:
                title = "取消置顶";
            default:
                break;

        }
        return title;
    }

    /**
     * 执行长按menu操作，需要确认时的提示信息。比如长按会话 -> 删除 -> 提示框进行二次确认
     *
     * @param tag
     * @return
     */
    public String contextConfirmPrompt(Context context, String tag) {
        String title = "未设置";
        switch (tag) {
            case ConversationContextMenuItemTags.TAG_CLEAR:
                title = "确认清空会话？";
                break;
            case ConversationContextMenuItemTags.TAG_REMOVE:
                title = "确认删除会话?";
                break;
        }
        return title;
    }

    /**
     * @param conversationInfo
     * @param itemTag
     * @return 返回true，将从context menu中排除
     */
    public boolean contextMenuItemFilter(Conversation conversationInfo, String itemTag) {
        return false;
    }

    protected <T extends View> T getView(int viewId) {
        View view;
        view = itemView.findViewById(viewId);
        return (T) view;
    }

    protected ChannelMsgItemViewHolder setViewVisibility(int viewId, int visibility) {
        View view = itemView.findViewById(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
