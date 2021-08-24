package com.stv.msgservice.ui.channel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.model.UserInfo;
import com.stv.msgservice.ui.GlideCircleWithBorder;
import com.stv.msgservice.ui.conversation.ConversationMemberAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelLatestUsedChatbotListAdapter extends RecyclerView.Adapter<ChannelLatestUsedChatbotListAdapter.MemberViewHolder>{
    private List<UserInfoEntity> members;
//    private Conversation conversationInfo;
//    private boolean enableAddMember;
//    private boolean enableRemoveMember;
    private ConversationMemberAdapter.OnMemberClickListener onMemberClickListener;

//    public ChannelLatestUsedChatbotListAdapter(Conversation conversationInfo, boolean enableAddMember, boolean enableRemoveMember) {
//        this.conversationInfo = conversationInfo;
//        this.enableAddMember = enableAddMember;
//        this.enableRemoveMember = enableRemoveMember;
//    }

    public void setMembers(List<UserInfoEntity> members) {
        this.members = members;
    }

    public void setOnMemberClickListener(ConversationMemberAdapter.OnMemberClickListener onMemberClickListener) {
        this.onMemberClickListener = onMemberClickListener;
    }

    @NonNull
    @Override
    public ChannelLatestUsedChatbotListAdapter.MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.conversation_item_member_info, parent, false);
        return new ChannelLatestUsedChatbotListAdapter.MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelLatestUsedChatbotListAdapter.MemberViewHolder holder, int position) {
        if (position < members.size()) {
            holder.bindUserInfo(members.get(position));
        } /*else {
            if (position == members.size()) {
                if (enableAddMember) {
                    holder.bindAddMember();
                } else if (enableRemoveMember) {
                    holder.bindRemoveMember();
                }
            } else if (position == members.size() + 1 && enableRemoveMember) {
                holder.bindRemoveMember();
            }
        }*/
    }

    @Override
    public int getItemCount() {
        if (members == null) {
            return 0;
        }
        int count = members.size();
//        if (enableAddMember) {
//            count++;
//        }
//        if (enableRemoveMember) {
//            count++;
//        }
        return count;
    }

    class MemberViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.portraitImageView)
        ImageView portraitImageView;
        @BindView(R2.id.nameTextView)
        TextView nameTextView;
        private UserInfo userInfo;
        private int type = TYPE_USER;
        private static final int TYPE_USER = 0;
        private static final int TYPE_ADD = 1;
        private static final int TYPE_REMOVE = 2;

        @OnClick(R2.id.portraitImageView)
        void onClick() {
            if (onMemberClickListener == null) {
                return;
            }
            switch (type) {
                case TYPE_USER:
                    if (userInfo != null) {
                        onMemberClickListener.onUserMemberClick(userInfo);
                    }
                    break;
                case TYPE_ADD:
                    onMemberClickListener.onAddMemberClick();
                    break;
                case TYPE_REMOVE:
                    onMemberClickListener.onRemoveMemberClick();
                    break;
                default:
                    break;
            }
        }

        public MemberViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindUserInfo(UserInfo userInfo) {
            if (userInfo == null) {
                nameTextView.setText("");
                portraitImageView.setImageResource(R.mipmap.avatar_def);
                return;
            }
            this.userInfo = userInfo;
            this.type = TYPE_USER;
            nameTextView.setVisibility(View.VISIBLE);
//            if (conversationInfo.conversation.type == Conversation.ConversationType.Group) {
//                nameTextView.setText(ChatManager.Instance().getGroupMemberDisplayName(conversationInfo.conversation.target, userInfo.uid));
//            } else {
//                nameTextView.setText(ChatManager.Instance().getUserDisplayName(userInfo.uid));
//            }
            Log.i("Junwang", "latest used chatbot list userName="+userInfo.getName()+", portrait url="+userInfo.getPortrait());
            nameTextView.setText(userInfo.getName());
            Glide.with(portraitImageView).load(userInfo.getPortrait()).apply(new RequestOptions().placeholder(R.mipmap.avatar_def)).transform(new CenterCrop(),new GlideCircleWithBorder()).into(portraitImageView);
        }

        public void bindAddMember() {
            nameTextView.setVisibility(View.GONE);
            portraitImageView.setImageResource(R.mipmap.ic_add_team_member);
            this.type = TYPE_ADD;

        }

        public void bindRemoveMember() {
            nameTextView.setVisibility(View.GONE);
            portraitImageView.setImageResource(R.mipmap.ic_remove_team_member);
            this.type = TYPE_REMOVE;
        }
    }

    public interface OnMemberClickListener {
        void onUserMemberClick(UserInfo userInfo);

        void onAddMemberClick();

        void onRemoveMemberClick();
    }
}
