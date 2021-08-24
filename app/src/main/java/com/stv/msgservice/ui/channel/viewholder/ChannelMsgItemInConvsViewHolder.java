package com.stv.msgservice.ui.channel.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.ConversationContextMenuItem;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.third.utils.TimeUtils;
import com.stv.msgservice.ui.channel.activity.ChannelConversationFragment;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.ui.conversation.message.viewholder.MessageContextMenuItemTags;
import com.stv.msgservice.ui.conversationlist.viewholder.ConversationContextMenuItemTags;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.OnClick;
import butterknife.Optional;

public abstract class ChannelMsgItemInConvsViewHolder  extends RecyclerView.ViewHolder {
    protected Fragment fragment;
    protected View itemView;
    protected Conversation conversationInfo;
    protected RecyclerView.Adapter adapter;
    protected ConversationListViewModel conversationListViewModel;
    protected Message message;
    protected int position;
    ImageView addToFavMsg;

    protected TextView channel_chatbot_timeTextView;
    private MessageViewModel.Factory factory;
    private MessageViewModel messageViewModel;

    public ChannelMsgItemInConvsViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(itemView);
        this.fragment = fragment;
        this.itemView = itemView;
        this.adapter = adapter;
        channel_chatbot_timeTextView = itemView.findViewById(R.id.channel_chatbot_timeTextView);
        addToFavMsg = itemView.findViewById(R.id.channel_fav_image_button);
        factory = new MessageViewModel.Factory(
                fragment.getActivity().getApplication(), 0);
        messageViewModel = new ViewModelProvider(fragment, factory)
                .get(MessageViewModel.class);
    }

    public void onBind(Message message, int position) {
        this.message = message;
        this.position = position;

        if(message.getSenderAddress() != null){
            channel_chatbot_timeTextView.setText(TimeUtils.getMsgFormatTime(message.getTime()));
            itemView.setBackgroundResource(R.drawable.selector_common_item/*conversationInfo.isTop ? R.drawable.selector_stick_top_item : R.drawable.selector_common_item*/);
        }

        try {
            onBind(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected abstract void onBind(Message message);

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
     * @param Message
     * @param itemTag
     * @return 返回true，将从context menu中排除
     */
    public boolean contextMenuItemFilter(Message message, String itemTag) {
        return false;
    }

    protected <T extends View> T getView(int viewId) {
        View view;
        view = itemView.findViewById(viewId);
        return (T) view;
    }

    protected ChannelMsgItemInConvsViewHolder setViewVisibility(int viewId, int visibility) {
        View view = itemView.findViewById(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
