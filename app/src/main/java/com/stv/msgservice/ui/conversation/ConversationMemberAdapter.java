package com.stv.msgservice.ui.conversation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.R2;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.UserInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationMemberAdapter extends RecyclerView.Adapter<ConversationMemberAdapter.MemberViewHolder> {
    private List<UserInfo> members;
    private Conversation conversationInfo;
    private boolean enableAddMember;
    private boolean enableRemoveMember;
    private OnMemberClickListener onMemberClickListener;

    public ConversationMemberAdapter(Conversation conversationInfo, boolean enableAddMember, boolean enableRemoveMember) {
        this.conversationInfo = conversationInfo;
        this.enableAddMember = enableAddMember;
        this.enableRemoveMember = enableRemoveMember;
    }

    public void setMembers(List<UserInfo> members) {
        this.members = members;
    }

    public void setMemberList(List<UserInfoEntity> users){
        if(users != null  && users.size() > 0){
            List<UserInfo> lists = new ArrayList<>();
            for(UserInfoEntity entity : users){
                lists.add((UserInfo) users);
            }
            this.members = lists;
        }
    }


    public void addMembers(List<UserInfo> members) {
        int startIndex = this.members.size();
        this.members.addAll(members);
        notifyItemRangeInserted(startIndex, members.size());
    }

    public void updateMember(UserInfo userInfo) {
        if (this.members == null) {
            return;
        }
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getUri().equals(userInfo.getUri())) {
                members.set(i, userInfo);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public void removeMembers(List<String> memberIds) {
        Iterator<UserInfo> iterator = members.iterator();
        while (iterator.hasNext()) {
            UserInfo userInfo = iterator.next();
            if (memberIds.contains(userInfo.getUri())) {
                iterator.remove();
                memberIds.remove(userInfo.getUri());
            }

            if (memberIds.size() == 0) {
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setOnMemberClickListener(OnMemberClickListener onMemberClickListener) {
        this.onMemberClickListener = onMemberClickListener;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.conversation_item_member_info, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        if (position < members.size()) {
            holder.bindUserInfo(members.get(position));
        } else {
            if (position == members.size()) {
                if (enableAddMember) {
                    holder.bindAddMember();
                } else if (enableRemoveMember) {
                    holder.bindRemoveMember();
                }
            } else if (position == members.size() + 1 && enableRemoveMember) {
                holder.bindRemoveMember();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (members == null) {
            return 0;
        }
        int count = members.size();
        if (enableAddMember) {
            count++;
        }
        if (enableRemoveMember) {
            count++;
        }
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
            nameTextView.setText(userInfo.getName());
            Glide.with(portraitImageView).load(userInfo.getPortrait()).apply(new RequestOptions().centerCrop().placeholder(R.mipmap.avatar_def)).into(portraitImageView);
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
