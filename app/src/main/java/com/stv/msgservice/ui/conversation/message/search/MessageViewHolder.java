package com.stv.msgservice.ui.conversation.message.search;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.viewmodel.UserInfoViewModel;
import com.stv.msgservice.third.utils.TimeUtils;
import com.stv.msgservice.ui.GlideApp;
import com.stv.msgservice.utils.UIUtils;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    @BindView(R2.id.portraitImageView)
    protected ImageView portraitImageView;
    @BindView(R2.id.nameTextView)
    protected TextView nameTextView;
    @BindView(R2.id.contentTextView)
    protected TextView contentTextView;
    @BindView(R2.id.timeTextView)
    protected TextView timeTextView;

    private Fragment fragment;
    private UserInfoViewModel userViewModel;

    public MessageViewHolder(Fragment fragment, View itemView) {
        super(itemView);
        this.fragment = fragment;
//        this.userViewModel = ViewModelProviders.of(fragment).get(UserInfoViewModel.class);
        ButterKnife.bind(this, itemView);
    }

    public void onBind(Message message) {
        if(message.getDirection() == MessageConstants.DIRECTION_IN){

        }
//        UserInfo sender = userViewModel.getUserInfo(message.sender, false);
//        if (sender != null) {
//            String senderName;
//            if (message.conversation.type == Conversation.ConversationType.Group) {
//                senderName = ChatManager.Instance().getGroupMemberDisplayName(message.conversation.target, sender.uid);
//            } else {
//                senderName = ChatManager.Instance().getUserDisplayName(sender);
//            }
//            nameTextView.setText(sender.getName());
//            GlideApp.with(portraitImageView).load(sender.getPortrait()).placeholder(R.mipmap.avatar_def).into(portraitImageView);
//        }
        UserInfoViewModel.Factory factory = new UserInfoViewModel.Factory(
                fragment.getActivity().getApplication(), null);

        final UserInfoViewModel userInfoViewModel = new ViewModelProvider(fragment, factory)
                .get(UserInfoViewModel.class);
        userInfoViewModel.getUserInfoByConversationId(message.getConversationId()).observe(fragment, userInfo -> {
            if(userInfo != null){
                if(message.getDirection() == MessageConstants.DIRECTION_IN){
                    String name = userInfo.getName();
                    String portrait = userInfo.getPortrait();
                    GlideApp.with(fragment)
                            .load(portrait)
                            .placeholder(R.mipmap.avatar_def)
                            .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dip2Px(fragment.getContext(), 4)))
                            .into(portraitImageView);
                    nameTextView.setText(name);
                }else{
                    portraitImageView.setImageResource(R.mipmap.avatar_def);
                }
            }
        });
//        if (message.content instanceof NotificationMessageContent) {
//            contentTextView.setText(((NotificationMessageContent) message.content).formatNotification(message));
//        } else {
//            contentTextView.setText(message.digest());
//        }
        contentTextView.setText(message.getContent());
        timeTextView.setText(TimeUtils.getMsgFormatTime(/*message.serverTime*/message.getTime()));
    }
}
