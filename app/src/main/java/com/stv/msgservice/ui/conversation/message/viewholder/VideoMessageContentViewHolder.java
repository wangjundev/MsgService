package com.stv.msgservice.ui.conversation.message.viewholder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.VideoMessageContent;
import com.stv.msgservice.ui.widget.BubbleImageView;
import com.stv.msgservice.utils.WeChatImageUtils;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 小视频尺寸展示高仿微信,并且带上时间
 * 占位图-缩略图-原图
 */
@MessageContentType(VideoMessageContent.class)
@EnableContextMenu
public class VideoMessageContentViewHolder extends MediaMessageContentViewHolder {
    private static final String TAG = "VideoMessageContentView";
    @BindView(R2.id.imageView)
    BubbleImageView imageView;
    @BindView(R2.id.playImageView)
    ImageView playImageView;

    @BindView(R2.id.time_tv)
    TextView time_tv;

    private  String imagePath ;

    public VideoMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
        Bitmap thumbnail = BitmapFactory.decodeFile(message.getThumbnailPath());
//        time_tv.setText(TimeConvertUtils.formatLongTime(videoMessageContent.getDuration()/1000));
        int width = 200;
        int height = 200;
        if(thumbnail != null) {
            int imageSize[] = WeChatImageUtils.getImageSizeByOrgSizeToWeChat(thumbnail.getWidth(), thumbnail.getHeight());
            width = imageSize[0] > 0 ? imageSize[0] : 200;
            height = imageSize[1] > 0 ? imageSize[1] : 200;
//            width = thumbnail.getWidth();
//            height = thumbnail.getHeight();
            imageView.getLayoutParams().width = width;
            imageView.getLayoutParams().height = height;
            Log.i("Junwang", "width="+width+", height="+height);
        }
        playImageView.setVisibility(View.VISIBLE);
//        if(FileUtils.isFileExists(videoMessageContent.localPath)){
//            imagePath = videoMessageContent.localPath;
//        }else {
//            imagePath = videoMessageContent.remoteUrl;
//        }
        imagePath = message.getAttachmentPath();
        loadMedia(thumbnail,imagePath,imageView);
//        loadImage(message.getThumbnailPath(), message.getAttachmentPath(), imageView);

    }

    @OnClick(R2.id.videoContentLayout)
    void play() {
        previewMM();
    }

    @Override
    public boolean contextMenuItemFilter(Message uiMessage, String tag) {
        if (MessageContextMenuItemTags.TAG_FORWARD.equals(tag)) {
            return true;
        } else {
            return super.contextMenuItemFilter(uiMessage, tag);
        }
    }

}
