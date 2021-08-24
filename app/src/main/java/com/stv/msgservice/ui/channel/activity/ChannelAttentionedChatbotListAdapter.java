package com.stv.msgservice.ui.channel.activity;

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

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChannelAttentionedChatbotListAdapter extends RecyclerView.Adapter<ChannelAttentionedChatbotListAdapter.ViewHolder>{
    private List<UserInfoEntity> members;
    private ChannelAttentionedChatbotListAdapter.OnMemberClickListener onMemberClickListener;

    public void setMembers(List<UserInfoEntity> members) {
        this.members = members;
    }

    public void setOnMemberClickListener(ChannelAttentionedChatbotListAdapter.OnMemberClickListener onMemberClickListener) {
        this.onMemberClickListener = onMemberClickListener;
    }

    @NonNull
    @Override
    public ChannelAttentionedChatbotListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.channel_attentioned_chatbot_list_item_view, parent, false);
        return new ChannelAttentionedChatbotListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelAttentionedChatbotListAdapter.ViewHolder holder, int position) {
        if (position < members.size()) {
            holder.bindUserInfo(members.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (members == null) {
            return 0;
        }
        int count = members.size();
        return count;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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

        public ViewHolder(View itemView) {
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
            Log.i("Junwang", "latest used chatbot list userName="+userInfo.getName()+", portrait url="+userInfo.getPortrait());
            nameTextView.setText(userInfo.getName());
            Glide.with(portraitImageView).load(userInfo.getPortrait()).apply(new RequestOptions().placeholder(R.mipmap.avatar_def)).transform(new CenterCrop(),new GlideCircleWithBorder()).into(portraitImageView);
        }
    }

    public interface OnMemberClickListener {
        void onUserMemberClick(UserInfo userInfo);

        void onAddMemberClick();

        void onRemoveMemberClick();
    }
}
