package com.stv.msgservice.ui.channel.viewholder;

import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.third.utils.TimeConvertUtils;
import com.stv.msgservice.ui.channel.ChannelCardSuggestionListAdapter;
import com.stv.msgservice.ui.videoplayer.ijk.IjkPlayer;
import com.stv.msgservice.ui.videoplayer.player.PlayerFactory;
import com.stv.msgservice.ui.videoplayer.player.SantiVideoView;
import com.stv.msgservice.ui.videoplayer.ui.StandardVideoController;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class ChannelVideoMessageContentViewHolder extends ChannelMsgItemViewHolder {
    ImageView channelVideoMsgImage;
    TextView channelVideoMsgTime;
    SantiVideoView channelVideoMsgVideoView;
    ImageView playImageView;
    Fragment mFragment;
    ChannelCardSuggestionListAdapter adpter;

    public ChannelVideoMessageContentViewHolder(Fragment fragment, RecyclerView.Adapter adapter, View itemView, View viewStubInflator) {
        super(fragment, adapter, itemView);
        mFragment = fragment;
        channelVideoMsgImage = viewStubInflator.findViewById(R.id.channel_video_msg_imageView);
        channelVideoMsgTime = viewStubInflator.findViewById(R.id.channel_videomsg_time);
        channelVideoMsgVideoView = viewStubInflator.findViewById(R.id.channel_videomsg_videoview);
        playImageView = viewStubInflator.findViewById(R.id.playImageView);
    }

    @Override
    public void onBind(MessageUserInfoEntity message) {
        Log.i("Junwang", "ChannelSingleCardMessageContentViewHolder onBind content=" + message.getContent());
        RequestOptions placeholderOptions = new RequestOptions();
        placeholderOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        placeholderOptions.placeholder(R.drawable.image_chat_placeholder);

        String thumbnail = message.getThumbnailPath();
        String imagePath = message.getAttachmentPath();
        if (thumbnail != null) {
            Glide.with(fragment)
                    .load(thumbnail)
                    .apply(placeholderOptions)
                    .into(channelVideoMsgImage);
        } else {
            Glide.with(fragment)
                    .load(imagePath)
                    .apply(placeholderOptions)
                    .into(channelVideoMsgImage);
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(imagePath);
        long duration = Long.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        channelVideoMsgTime.setText(TimeConvertUtils.formatLongTime(duration/1000));
        playImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelVideoMsgImage.setVisibility(View.INVISIBLE);
                channelVideoMsgTime.setVisibility(View.INVISIBLE);
                playImageView.setVisibility(View.INVISIBLE);
                channelVideoMsgVideoView.setVisibility(View.VISIBLE);

                StandardVideoController standardVideoController = new StandardVideoController(fragment.getContext());
                channelVideoMsgVideoView.setVideoController(standardVideoController);
                channelVideoMsgVideoView.setUrl(imagePath);
                channelVideoMsgVideoView.setPlayerFactory(new PlayerFactory<IjkPlayer>() {
                    @Override
                    public IjkPlayer createPlayer() {
                        return new IjkPlayer() {
                            @Override
                            public void setOptions() {
                                //精准seek
                                mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);
                            }
                        };
                    }
                });
                channelVideoMsgVideoView.start();
            }
        });
    }
}
