package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.ui.GlideApp;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.ConversationMessageAdapter;

import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * 普通消息
 */
public abstract class NormalMessageContentViewHolder extends MessageContentViewHolder {
    @BindView(R2.id.portraitImageView)
    ImageView portraitImageView;
    @BindView(R2.id.errorLinearLayout)
    LinearLayout errorLinearLayout;
    @BindView(R2.id.nameTextView)
    TextView nameTextView;
    @BindView(R2.id.progressBar)
    ProgressBar progressBar;
    @BindView(R2.id.checkbox)
    CheckBox checkBox;

    @BindView(R2.id.singleReceiptImageView)
    @Nullable
    ImageView singleReceiptImageView;

//    @BindView(R2.id.groupReceiptFrameLayout)
//    @Nullable
//    FrameLayout groupReceiptFrameLayout;

    @BindView(R2.id.deliveryProgressBar)
    @Nullable
    ProgressBar deliveryProgressBar;
    @BindView(R2.id.readProgressBar)
    @Nullable
    ProgressBar readProgressBar;
    private Fragment mFragment;

    public NormalMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
    }

    @Override
    public void onBind(Message message, int position) {
        super.onBind(message, position);
        this.message = message;
        this.position = position;

        setSenderAvatar(message);
        setSenderName(message);
        setSendStatus(message);
        try {
            onBind(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (message.isFocus) {
//            highlightItem(itemView, message);
//        }
    }

    protected abstract void onBind(Message message);

    /**
     * when animation finish, do not forget to set  Message#isFocus to {@code true}
     *
     * @param itemView the item view
     * @param message  the message to highlight
     */
    protected void highlightItem(View itemView, Message message) {
        Animation animation = new AlphaAnimation((float) 0.4, (float) 0.2);
        itemView.setBackgroundColor(itemView.getResources().getColor(R.color.colorPrimary));
        animation.setRepeatCount(2);
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                itemView.setBackground(null);
//                message.isFocus = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        itemView.startAnimation(animation);
    }

    // TODO 也用注解来做？
    public boolean checkable(Message message) {
        return true;
    }

    @Optional
    @OnClick(R2.id.errorLinearLayout)
    public void onRetryClick(View itemView) {
        new MaterialDialog.Builder(fragment.getContext())
                .content("重新发送?")
                .negativeText("取消")
                .positiveText("重发")
                .onPositive((dialog, which) -> {
                    ((ConversationActivity)(fragment.getActivity())).resendMsg((MessageEntity) message);
                })
                .build()
                .show();
    }

//    @Optional
//    @OnClick(R2.id.groupReceiptFrameLayout)
//    public void OnGroupMessageReceiptClick(View itemView) {
//        ((ConversationMessageAdapter) adapter).onGroupMessageReceiptClick(message.message);
//    }

//    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_RECALL, priority = 10)
//    public void recall(View itemView, Message message) {
//        messageViewModel.recallMessage(message.message);
//    }

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
                        ((ConversationActivity)mFragment.getActivity()).deleteMsg(message);
                    }
                })
                .show();
    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_FORWARD, priority = 11)
    public void forwardMessage(View itemView, Message message) {
//        Intent intent = new Intent(fragment.getContext(), ForwardActivity.class);
//        intent.putExtra("message", message.toString());
//        fragment.startActivity(intent);
    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_MULTI_CHECK, priority = 13)
    public void checkMessage(View itemView, Message message) {
        fragment.toggleMultiMessageMode(message);
    }

//    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_CHANEL_PRIVATE_CHAT, priority = 12)
//    public void startChanelPrivateChat(View itemView, Message message) {
//        Intent intent = ConversationActivity.buildConversationIntent(fragment.getContext(), Conversation.ConversationType.Channel, message.message.conversation.target, message.message.conversation.line, message.message.sender);
//        fragment.startActivity(intent);
//    }

//    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_QUOTE, priority = 14)
//    public void quoteMessage(View itemView, Message message) {
//        fragment.getConversationInputPanel().quoteMessage(message.message);
//    }

//    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_FAV, confirm = false, priority = 12)
//    public void fav(View itemView, Message message) {
//        AppServiceProvider appServiceProvider = WfcUIKit.getWfcUIKit().getAppServiceProvider();
//        FavoriteItem favoriteItem = FavoriteItem.fromMessage(message.message);
//
//        appServiceProvider.addFavoriteItem(favoriteItem, new SimpleCallback<Void>() {
//            @Override
//            public void onUiSuccess(Void aVoid) {
//                Toast.makeText(fragment.getContext(), "fav ok", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onUiFailure(int code, String msg) {
//                Toast.makeText(fragment.getContext(), "fav error: " + code, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        String title = "未设置";
        switch (tag) {
//            case MessageContextMenuItemTags.TAG_RECALL:
//                title = "撤回";
//                break;
            case MessageContextMenuItemTags.TAG_DELETE:
                title = "删除";
                break;
            case MessageContextMenuItemTags.TAG_FORWARD:
                title = "转发";
                break;
//            case MessageContextMenuItemTags.TAG_QUOTE:
//                title = "引用";
//                break;
            case MessageContextMenuItemTags.TAG_MULTI_CHECK:
                title = "多选";
                break;
//            case MessageContextMenuItemTags.TAG_CHANEL_PRIVATE_CHAT:
//                title = "私聊";
//                break;
            case MessageContextMenuItemTags.TAG_FAV:
                title = "收藏";
                break;
            case MessageContextMenuItemTags.TAG_QR:
                title = null;
                break;
            default:
                break;
        }
        return title;
    }

    @Override
    public String contextConfirmPrompt(Context context, String tag) {
        String title = "未设置";
        switch (tag) {
            case MessageContextMenuItemTags.TAG_DELETE:
                title = "确认删除此消息";
                break;
            default:
                break;
        }
        return title;
    }

    @Override
    public boolean contextMenuItemFilter(Message uiMessage, String tag) {
//        Message message = uiMessage.message;
//        if (MessageContextMenuItemTags.TAG_RECALL.equals(tag)) {
//            String userId = ChatManager.Instance().getUserId();
//            if (message.conversation.type == Conversation.ConversationType.Group) {
//                GroupViewModel groupViewModel = ViewModelProviders.of(fragment).get(GroupViewModel.class);
//                GroupInfo groupInfo = groupViewModel.getGroupInfo(message.conversation.target, false);
//                if (groupInfo != null && userId.equals(groupInfo.owner)) {
//                    return false;
//                }
//                GroupMember groupMember = groupViewModel.getGroupMember(message.conversation.target, ChatManager.Instance().getUserId());
//                if (groupMember != null && (groupMember.type == GroupMember.GroupMemberType.Manager
//                        || groupMember.type == GroupMember.GroupMemberType.Owner)) {
//                    return false;
//                }
//            }
//
//            long delta = ChatManager.Instance().getServerDeltaTime();
//            long now = System.currentTimeMillis();
//            if (message.direction == MessageDirection.Send
//                    && TextUtils.equals(message.sender, ChatManager.Instance().getUserId())
//                    && now - (message.serverTime - delta) < Config.RECALL_TIME_LIMIT * 1000) {
//                return false;
//            } else {
//                return true;
//            }
//        }
//
//        // 只有channel 主可以发起
//        if (MessageContextMenuItemTags.TAG_CHANEL_PRIVATE_CHAT.equals(tag)) {
//            if (uiMessage.message.conversation.type == Conversation.ConversationType.Channel
//                    && uiMessage.message.direction == MessageDirection.Receive) {
//                return false;
//            }
//            return true;
//        }
//
//        // 只有部分消息支持引用
//        if (MessageContextMenuItemTags.TAG_QUOTE.equals(tag)) {
//            MessageContent messageContent = message.content;
//            if (messageContent instanceof TextMessageContent
//                    || messageContent instanceof FileMessageContent
//                    || messageContent instanceof VideoMessageContent
//                    || messageContent instanceof StickerMessageContent
//                    || messageContent instanceof ImageMessageContent) {
//                return false;
//            }
//            return true;
//        }
//
//        // 只有部分消息支持引用
//        if (MessageContextMenuItemTags.TAG_FAV.equals(tag)) {
//            MessageContent messageContent = message.content;
//            if (messageContent instanceof TextMessageContent
//                    || messageContent instanceof FileMessageContent
//                    || messageContent instanceof CompositeMessageContent
//                    || messageContent instanceof VideoMessageContent
//                    || messageContent instanceof SoundMessageContent
//                    || messageContent instanceof ImageMessageContent) {
//                return false;
//            }
//            return true;
//        }

        return false;
    }

    private void setSenderAvatar(Message item) {
        // TODO get user info from viewModel
//        UserInfo userInfo = ChatManagerHolder.gChatManager.getUserInfo(item.sender, false);
//        if (portraitImageView != null) {
//            GlideApp
//                    .with(fragment)
//                    .load(userInfo.getPortrait())
//                    .transforms(new CenterCrop(), new RoundedCorners(10))
//                    .placeholder(R.mipmap.avatar_def)
//                    .into(portraitImageView);
//        }
//
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                fragment.getActivity().getApplication(), null);

        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(fragment, factory)
                .get(UserInfoViewModel.class);
        userInfoViewModel.getUserInfoByConversationId(item.getConversationId()).observe(fragment, userInfo -> {
            if(userInfo != null){
                if(item.getDirection() == MessageConstants.DIRECTION_IN){
                    String name = userInfo.getName();
                    String portrait = userInfo.getPortrait();
                    GlideApp.with(fragment)
                            .load(portrait)
                            .placeholder(R.mipmap.avatar_def)
                            .transform(new CenterCrop(),new GlideCircleWithBorder())
//                            .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
                            .into(portraitImageView);
                    nameTextView.setText(name);
                }else{
                    portraitImageView.setImageResource(R.mipmap.avatar_def);
//                    GlideApp.with(fragment)
//                            .load(R.mipmap.avatar_def)
//                            .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
//                            .into(portraitImageView);
                }
            }
        });
    }

    private void setSenderName(Message item) {
//        if (item.conversation.type == Conversation.ConversationType.Single) {
//            nameTextView.setVisibility(View.GONE);
//        } else if (item.conversation.type == Conversation.ConversationType.Group) {
//            showGroupMemberAlias(message.message.conversation, message.message, message.message.sender);
//        } else {
//            // todo
//        }
    }

//    private void showGroupMemberAlias(Conversation conversation, Message message, String sender) {
//        UserViewModel userViewModel = ViewModelProviders.of(fragment).get(UserViewModel.class);
//        if (!"1".equals(userViewModel.getUserSetting(UserSettingScope.GroupHideNickname, conversation.target)) || message.direction == MessageDirection.Send) {
//            nameTextView.setVisibility(View.GONE);
//            return;
//        }
//        nameTextView.setVisibility(View.VISIBLE);
//        // TODO optimize 缓存userInfo吧
////        if (Conversation.equals(nameTextView.getTag(), sender)) {
////            return;
////        }
//        GroupViewModel groupViewModel = ViewModelProviders.of(fragment).get(GroupViewModel.class);
//
//        nameTextView.setText(groupViewModel.getGroupMemberDisplayName(conversation.target, sender));
//        nameTextView.setTag(sender);
//    }

    protected boolean showMessageReceipt(Message message) {
//        ContentTag tag = message.content.getClass().getAnnotation(ContentTag.class);
//        return (tag != null && (tag.flag() == PersistFlag.Persist_And_Count));
        return true;
    }

    protected void setSendStatus(Message item) {
        int sentStatus = item.getMessageStatus();
        if (item.getDirection() == MessageConstants.DIRECTION_IN) {
            return;
        }
        if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_SENDING) {
            Log.i("Junwang", "setSendStatus sending");
            progressBar.setVisibility(View.VISIBLE);
            errorLinearLayout.setVisibility(View.GONE);
            return;
        } else if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_FAILED) {
            progressBar.setVisibility(View.GONE);
            errorLinearLayout.setVisibility(View.VISIBLE);
            return;
        } else if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE) {
            progressBar.setVisibility(View.GONE);
            errorLinearLayout.setVisibility(View.GONE);
        } /*else if (sentStatus == MessageStatus.Readed) {
            progressBar.setVisibility(View.GONE);
            errorLinearLayout.setVisibility(View.GONE);
            return;
        }*/

//        if (!ChatManager.Instance().isReceiptEnabled() || !ChatManager.Instance().isUserEnableReceipt() || !showMessageReceipt(message.message)) {
//            return;
//        }

        Map<String, Long> deliveries = ((ConversationMessageAdapter) adapter).getDeliveries();
        Map<String, Long> readEntries = ((ConversationMessageAdapter) adapter).getReadEntries();

        /*if (item.conversation.type == Conversation.ConversationType.Single)*/ {
//            singleReceiptImageView.setVisibility(View.VISIBLE);
////            groupReceiptFrameLayout.setVisibility(View.GONE);
//            Long readTimestamp = readEntries != null && !readEntries.isEmpty() ? readEntries.get(message.message.conversation.target) : null;
//            Long deliverTimestamp = deliveries != null && !deliveries.isEmpty() ? deliveries.get(message.message.conversation.target) : null;


//            if (readTimestamp != null && readTimestamp >= message.message.serverTime) {
//                ImageViewCompat.setImageTintList(singleReceiptImageView, null);
//                return;
//            }
//            if (deliverTimestamp != null && deliverTimestamp >= message.message.serverTime) {
//                ImageViewCompat.setImageTintList(singleReceiptImageView, ColorStateList.valueOf(ContextCompat.getColor(fragment.getContext(), R.color.gray)));
//            }
        } /*else if (item.conversation.type == Conversation.ConversationType.Group) {
            singleReceiptImageView.setVisibility(View.GONE);

            if (sentStatus == MessageStatus.Sent) {
                if (item.content instanceof CallStartMessageContent || (item.content.getPersistFlag().ordinal() & 0x2) == 0) {
                    groupReceiptFrameLayout.setVisibility(View.GONE);
                } else {
                    groupReceiptFrameLayout.setVisibility(View.VISIBLE);
                }
                int deliveryCount = 0;
                if (deliveries != null) {
                    for (Map.Entry<String, Long> delivery : deliveries.entrySet()) {
                        if (delivery.getValue() >= item.serverTime) {
                            deliveryCount++;
                        }
                    }
                }
                int readCount = 0;
                if (readEntries != null) {
                    for (Map.Entry<String, Long> readEntry : readEntries.entrySet()) {
                        if (readEntry.getValue() >= item.serverTime) {
                            readCount++;
                        }
                    }
                }

                GroupInfo groupInfo = ChatManager.Instance().getGroupInfo(item.conversation.target, false);
                if (groupInfo == null) {
                    return;
                }
                deliveryProgressBar.setMax(groupInfo.memberCount - 1);
                deliveryProgressBar.setProgress(deliveryCount);
                readProgressBar.setMax(groupInfo.memberCount - 1);
                readProgressBar.setProgress(readCount);
            } else {
                groupReceiptFrameLayout.setVisibility(View.GONE);
            }
        }*/
    }
}
