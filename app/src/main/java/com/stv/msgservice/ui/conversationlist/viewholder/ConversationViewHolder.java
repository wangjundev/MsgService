package com.stv.msgservice.ui.conversationlist.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stv.msgservice.MainActivity;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.ConversationContextMenuItem;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.viewmodel.ConversationListViewModel;
import com.stv.msgservice.third.utils.TimeUtils;
import com.stv.msgservice.ui.conversation.ConversationActivity;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.stv.msgservice.datamodel.constants.MessageConstants.BUGLE_STATUS_OUTGOING_FAILED;
import static com.stv.msgservice.datamodel.constants.MessageConstants.BUGLE_STATUS_OUTGOING_SENDING;

@SuppressWarnings("unused")
public abstract class ConversationViewHolder extends RecyclerView.ViewHolder {
    protected Fragment fragment;
    protected View itemView;
    protected Conversation conversationInfo;
    protected RecyclerView.Adapter adapter;
    protected ConversationListViewModel conversationListViewModel;
//    private MessageViewModel messageViewModel;

    @BindView(R2.id.nameTextView)
    protected TextView nameTextView;
    @BindView(R2.id.timeTextView)
    protected TextView timeTextView;
    @BindView(R2.id.portraitImageView)
    protected ImageView portraitImageView;
    @BindView(R2.id.slient)
    protected ImageView silentImageView;
    @BindView(R2.id.unreadCountTextView)
    protected TextView unreadCountTextView;
    @BindView(R2.id.redDotView)
    protected View redDotView;
    @BindView(R2.id.contentTextView)
    protected TextView contentTextView;
    @BindView(R2.id.promptTextView)
    protected TextView promptTextView;

    @BindView(R2.id.statusImageView)
    protected ImageView statusImageView;

    public ConversationViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(itemView);
        this.fragment = fragment;
        this.itemView = itemView;
        this.adapter = adapter;
        ButterKnife.bind(this, itemView);
//        conversationListViewModel = ViewModelProviders
//                .of(fragment, new ConversationListViewModelFactory(Arrays.asList(Conversation.ConversationType.Single, Conversation.ConversationType.Group), Arrays.asList(0)))
//                .get(ConversationListViewModel.class);
//        conversationViewModel = ViewModelProviders.of(fragment).get(ConversationViewModel.class);
        conversationListViewModel = new ViewModelProvider(fragment).get(ConversationListViewModel.class);
//        messageViewModel = new ViewModelProvider(fragment).get(MessageViewModel.class);
    }

    final public void onBind(Conversation conversationInfo, int position) {
        this.conversationInfo = conversationInfo;
        onBind(conversationInfo);
    }

    /**
     * ?????????????????????
     *
     * @param conversationInfo
     */
    protected abstract void onBindConversationInfo(Conversation conversationInfo);

    protected abstract void sendTextMsg(String destination, String text);

    public void onBind(Conversation conversationInfo) {
        onBindConversationInfo(conversationInfo);

        timeTextView.setText(TimeUtils.getMsgFormatTime(conversationInfo.getLastTimestamp()));
        silentImageView.setVisibility(View.GONE/*conversationInfo.isSilent ? View.VISIBLE : View.GONE*/);
        statusImageView.setVisibility(View.GONE);

        itemView.setBackgroundResource(R.drawable.selector_common_item/*conversationInfo.isTop ? R.drawable.selector_stick_top_item : R.drawable.selector_common_item*/);
        redDotView.setVisibility(View.GONE);
//        if (conversationInfo.isSilent) {
//            if (conversationInfo.unreadCount.unread > 0) { // ????????????
//                unreadCountTextView.setText("");
//                unreadCountTextView.setVisibility(View.GONE);
//                redDotView.setVisibility(View.VISIBLE);
//            } else {
//                unreadCountTextView.setVisibility(View.GONE);
//            }
//        } else {
//            if (conversationInfo.unreadCount.unread > 0) {
//                unreadCountTextView.setVisibility(View.VISIBLE);
//                unreadCountTextView.setText(conversationInfo.unreadCount.unread > 99 ? "99+" : conversationInfo.unreadCount.unread + "");
//            } else {
//                unreadCountTextView.setVisibility(View.GONE);
//            }
//        }
//        int count = conversationListViewModel.getUnreadCount(conversationInfo.getId());
        int count = conversationInfo.getUnreadCount();
        if (count > 0) {
            unreadCountTextView.setVisibility(View.VISIBLE);
            unreadCountTextView.setText(count > 99 ? "99+" : count + "");
        } else {
            unreadCountTextView.setVisibility(View.GONE);
        }


        /*Draft draft = Draft.fromDraftJson(conversationInfo.draft);
        if (draft != null) {
            String draftString = draft.getContent() != null ? draft.getContent() : "[??????]";
            MoonUtils.identifyFaceExpression(fragment.getActivity(), contentTextView, draft.getContent(), ImageSpan.ALIGN_BOTTOM);
            setViewVisibility(R.id.promptTextView, View.VISIBLE);
            setViewVisibility(R.id.contentTextView, View.VISIBLE);
        } else*/ {
//            if (conversationInfo.unreadCount.unreadMentionAll > 0 || conversationInfo.unreadCount.unreadMention > 0) {
//                promptTextView.setText("[??????@???]");
//                promptTextView.setVisibility(View.VISIBLE);
//            } else {
//                promptTextView.setVisibility(View.GONE);
//            }
            promptTextView.setVisibility(View.GONE);

            setViewVisibility(R.id.contentTextView, View.VISIBLE);
//            MoonUtils.identifyFaceExpression(fragment.getActivity(), contentTextView, "", ImageSpan.ALIGN_BOTTOM);
//            if (conversationInfo.lastMessage != null && conversationInfo.lastMessage.content != null) {
//                String content = "";
//                Message lastMessage = conversationInfo.lastMessage;
//                // the message maybe invalid
//                try {
//                    if (conversationInfo.conversation.type == Conversation.ConversationType.Group
//                            && lastMessage.direction == MessageDirection.Receive
//                            && !(lastMessage.content instanceof NotificationMessageContent)) {
//                        GroupViewModel groupViewModel = ViewModelProviders.of(fragment).get(GroupViewModel.class);
//                        String senderDisplayName = groupViewModel.getGroupMemberDisplayName(conversationInfo.conversation.target, conversationInfo.lastMessage.sender);
//                        content = senderDisplayName + ":" + lastMessage.digest();
//                    } else {
//                        content = lastMessage.digest();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                content = WfcTextUtils.htmlToText(content);
//                MoonUtils.identifyFaceExpression(fragment.getActivity(), contentTextView, content, ImageSpan.ALIGN_BOTTOM);
//
//                switch (lastMessage.status) {
//                    case Sending:
//                        statusImageView.setVisibility(View.VISIBLE);
//                        // TODO update sending image resource
//                        statusImageView.setImageResource(R.mipmap.ic_sending);
//                        break;
//                    case Send_Failure:
//                        statusImageView.setVisibility(View.VISIBLE);
//                        statusImageView.setImageResource(R.mipmap.img_error);
//                        break;
//                    default:
//                        statusImageView.setVisibility(View.GONE);
//                        break;
//                }
//
//            } else {
//                contentTextView.setText("");
//            }

            switch (conversationInfo.getLatestMessageStatus()){
                case BUGLE_STATUS_OUTGOING_SENDING:
                    statusImageView.setVisibility(View.VISIBLE);
                    // TODO update sending image resource
                    statusImageView.setImageResource(R.mipmap.ic_sending);
                    break;
                case BUGLE_STATUS_OUTGOING_FAILED:
                    statusImageView.setVisibility(View.VISIBLE);
                    statusImageView.setImageResource(R.mipmap.img_error);
                    break;
                default:
                    statusImageView.setVisibility(View.GONE);
                    break;
            }
            if(conversationInfo.getDraftSnippetText() != null){
                contentTextView.setText("[??????]" + conversationInfo.getDraftSnippetText());
            }
            else if(conversationInfo.getSnippetText() != null){
                contentTextView.setText(conversationInfo.getSnippetText());
            }else {
                contentTextView.setText("");
            }
        }
    }

    public void onClick(View itemView) {
        Intent intent = new Intent(fragment.getActivity(), ConversationActivity.class);
        intent.putExtra("conversation", (ConversationEntity)conversationInfo);
        fragment.startActivity(intent);
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

//    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_CLEAR,
//            confirm = false,
//            priority = 0)
//    public void clearMessages(View itemView, ConversationInfo conversationInfo) {
//        new MaterialDialog.Builder(fragment.getActivity())
//                .items("??????????????????", "??????????????????")
//                .itemsCallback(new MaterialDialog.ListCallback() {
//                    @Override
//                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {
//                        if (position == 0) {
//                            conversationViewModel.clearConversationMessage(conversationInfo.conversation);
//                        } else {
//                            conversationViewModel.clearRemoteConversationMessage(conversationInfo.conversation);
//                        }
//                    }
//                })
//                .show();
//    }

    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_TOP, priority = 1)
    public void stickConversationTop(View itemView, Conversation conversationInfo) {
//        conversationListViewModel.setConversationTop(conversationInfo, true);
    }

    @ConversationContextMenuItem(tag = ConversationContextMenuItemTags.TAG_CANCEL_TOP, priority = 2)
    public void cancelStickConversationTop(View itemView, Conversation conversationInfo) {
//        conversationListViewModel.setConversationTop(conversationInfo, false);
    }

    /**
     * ???????????????context menu?????????
     *
     * @param tag
     * @return
     */
    public String contextMenuTitle(Context context, String tag) {
        String title = "?????????";
        switch (tag) {
            case ConversationContextMenuItemTags.TAG_CLEAR:
                title = "????????????";
                break;
            case ConversationContextMenuItemTags.TAG_REMOVE:
                title = "????????????";
                break;
            case ConversationContextMenuItemTags.TAG_TOP:
                title = "??????";
                break;
            case ConversationContextMenuItemTags.TAG_CANCEL_TOP:
                title = "????????????";
            default:
                break;

        }
        return title;
    }

    /**
     * ????????????menu???????????????????????????????????????????????????????????? -> ?????? -> ???????????????????????????
     *
     * @param tag
     * @return
     */
    public String contextConfirmPrompt(Context context, String tag) {
        String title = "?????????";
        switch (tag) {
            case ConversationContextMenuItemTags.TAG_CLEAR:
                title = "?????????????????????";
                break;
            case ConversationContextMenuItemTags.TAG_REMOVE:
                title = "???????????????????";
                break;
        }
        return title;
    }

    /**
     * @param conversationInfo
     * @param itemTag
     * @return ??????true?????????context menu?????????
     */
    public boolean contextMenuItemFilter(Conversation conversationInfo, String itemTag) {
//        if (ConversationContextMenuItemTags.TAG_TOP.equals(itemTag)) {
//            return conversationInfo.isTop;
//        }
//
//        if (ConversationContextMenuItemTags.TAG_CANCEL_TOP.equals(itemTag)) {
//            return !conversationInfo.isTop;
//        }
        return false;
    }

    protected <T extends View> T getView(int viewId) {
        View view;
        view = itemView.findViewById(viewId);
        return (T) view;
    }

    protected ConversationViewHolder setViewVisibility(int viewId, int visibility) {
        View view = itemView.findViewById(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
