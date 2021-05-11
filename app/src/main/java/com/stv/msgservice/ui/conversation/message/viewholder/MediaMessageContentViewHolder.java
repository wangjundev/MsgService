package com.stv.msgservice.ui.conversation.message.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.ConversationMessageAdapter;
import com.stv.msgservice.ui.multimedia.MMPreviewActivity;
import com.stv.msgservice.ui.multimedia.MediaEntry;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MediaMessageContentViewHolder extends NormalMessageContentViewHolder {

    /**
     * 小视频，图片 占位图的配置
     */
    protected RequestOptions placeholderOptions = new RequestOptions();

    public MediaMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
        placeholderOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        placeholderOptions.centerCrop();
        placeholderOptions.placeholder(R.drawable.image_chat_placeholder);
    }

    @Override
    protected void onBind(Message message) {
//        if (message.isDownloading) {
        if(message.getMessageStatus() == MessageConstants.BUGLE_STATUS_INCOMING_DOWNLOADING){
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    protected void previewMM() {
        List<MessageEntity> messages = ((ConversationMessageAdapter) adapter).getMessages();
        List<MediaEntry> entries = new ArrayList<>();
        MessageEntity msg;

        int current = 0;
        int index = 0;
        for (int i = 0; i < messages.size(); i++) {
            msg = messages.get(i);
            if (msg.getMessageType() != MessageConstants.CONTENT_TYPE_IMAGE
                    && msg.getMessageType() != MessageConstants.CONTENT_TYPE_VIDEO) {
                continue;
            }
            MediaEntry entry = new MediaEntry();
            if (msg.getMessageType() == MessageConstants.CONTENT_TYPE_IMAGE) {
                entry.setType(MediaEntry.TYPE_IMAGE);
                Bitmap bitmap = BitmapFactory.decodeFile(msg.getThumbnailPath());
                entry.setThumbnail(bitmap);

            } else {
                entry.setType(MediaEntry.TYPE_VIDEO);
                Bitmap bitmap = BitmapFactory.decodeFile(msg.getThumbnailPath());
                entry.setThumbnail(bitmap);
//                entry.setThumbnail(((VideoMessageContent) msg.message.content).getThumbnail());
            }
            entry.setMediaUrl(msg.getAttachmentPath());
//            entry.setMediaLocalPath(((MediaMessageContent) msg.message.content).localPath);
            entry.setMediaLocalPath(msg.getAttachmentPath());
            entries.add(entry);

            if (message.getId() == msg.getId()) {
                current = index;
            }
            index++;
        }
        if (entries.isEmpty()) {
            return;
        }
        MMPreviewActivity.previewMedia(fragment.getContext(), entries, current);
    }

    /**
     * 图片 和小视频 加载的地方
     * 策略是先加载缩略图，在加载原图
     * dhl
     * @param thumbnail
     * @param imagePath
     * @param imageView
     */
    protected void loadMedia(Bitmap thumbnail, String imagePath, ImageView imageView){
        RequestBuilder<Drawable> thumbnailRequest = null;
        if(thumbnail != null) {
            thumbnailRequest = Glide
                    .with(fragment)
                    .load(thumbnail);
        }else{
            thumbnailRequest = Glide
                    .with(fragment)
                    .load(R.drawable.image_chat_placeholder);
        }
        Glide.with(fragment)
                .load(imagePath)
                .thumbnail(thumbnailRequest)
                .apply(placeholderOptions)
                .into(imageView);
    }
}
