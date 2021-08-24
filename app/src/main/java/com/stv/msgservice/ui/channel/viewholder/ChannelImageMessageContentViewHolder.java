package com.stv.msgservice.ui.channel.viewholder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.ui.channel.ChannelCardSuggestionListAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

public class ChannelImageMessageContentViewHolder extends ChannelMsgItemViewHolder {
    ImageView channelImageMsgImage;
    Fragment mFragment;
    ChannelCardSuggestionListAdapter adpter;

    public ChannelImageMessageContentViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView, View viewStubInflator) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
        channelImageMsgImage = viewStubInflator.findViewById(R.id.channel_image_msg_imageView);
    }

    @Override
    public void onBind(MessageUserInfoEntity message) {
        Log.i("Junwang", "ChannelSingleCardMessageContentViewHolder onBind content="+message.getContent());
        RequestOptions placeholderOptions = new RequestOptions();
        placeholderOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        placeholderOptions.placeholder(R.drawable.image_chat_placeholder);

        String thumbnail = message.getThumbnailPath();
        String imagePath = message.getAttachmentPath();
        if(thumbnail != null){
            Glide.with(fragment)
                    .load(thumbnail)
                    .apply(placeholderOptions)
                    .into(channelImageMsgImage);
        }else{
            Glide.with(fragment)
                    .load(imagePath)
                    .apply(placeholderOptions)
                    .into(channelImageMsgImage);
        }
    }
}
