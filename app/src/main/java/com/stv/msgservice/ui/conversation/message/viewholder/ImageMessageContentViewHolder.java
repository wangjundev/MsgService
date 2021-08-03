package com.stv.msgservice.ui.conversation.message.viewholder;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.ImageMessageContent;
import com.stv.msgservice.ui.widget.BubbleImageView;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * 图片尺寸展示高仿微信
 * 占位图-缩略图-原图
 */
@MessageContentType(ImageMessageContent.class)
@EnableContextMenu
public class ImageMessageContentViewHolder extends MediaMessageContentViewHolder {

    private static final String TAG = "ImageMessageContentView";
    @BindView(R2.id.imageView)
    BubbleImageView imageView;

    private String imagePath;

    public ImageMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
//        Bitmap thumbnail = BitmapFactory.decodeFile(message.getThumbnailPath());
//        Bitmap image = BitmapFactory.decodeFile(message.getAttachmentPath());
////        int imageSize[] = WeChatImageUtils.getImageSizeByOrgSizeToWeChat((int) imageMessage.getImageWidth(), (int) imageMessage.getImageHeight());
//        int imageSize[] = WeChatImageUtils.getImageSizeByOrgSizeToWeChat((int) image.getWidth(), (int) image.getHeight());
//        int width = imageSize[0] > 0 ? imageSize[0] : 200;
//        int height = imageSize[1] > 0 ? imageSize[1] : 200;
//        imageView.getLayoutParams().width = width;
//        imageView.getLayoutParams().height = height;
////        if (FileUtils.isFileExists(imageMessage.localPath)) {
////            imagePath = imageMessage.localPath;
////        } else {
////            imagePath = imageMessage.remoteUrl;
////        }
//        imagePath = message.getAttachmentPath();
//        loadMedia(thumbnail, imagePath, imageView);
        loadImage(message.getThumbnailPath(), message.getAttachmentPath(), imageView);
    }

    @OnClick(R2.id.imageView)
    void preview() {
        Log.i("Junwang", "onClick time="+ SystemClock.currentThreadTimeMillis());
        previewMM();
    }

    @Override
    protected void setSendStatus(Message item) {
        super.setSendStatus(item);
//        MessageContent msgContent = item.content;
        if (true/*msgContent instanceof ImageMessageContent*/) {
//            boolean isSend = item.direction == MessageDirection.Send;
            boolean isSend = (item.getMessageStatus() == MessageConstants.BUGLE_STATUS_OUTGOING_SENDING
                            || item.getMessageStatus() == MessageConstants.BUGLE_STATUS_OUTGOING_FAILED
                            || item.getMessageStatus() == MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if (isSend) {
//                MessageStatus sentStatus = item.status;
                int sentStatus = item.getMessageStatus();
                if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_SENDING) {
//                    imageView.setPercent(message.getPercent());
//                    imageView.setProgressVisible(true);
//                    imageView.showShadow(true);
                } else if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_FAILED/*MessageStatus.Send_Failure*/) {
                    imageView.setProgressVisible(false);
                    imageView.showShadow(false);
                } else if (sentStatus == MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE/*MessageStatus.Sent*/) {
                    imageView.setProgressVisible(false);
                    imageView.showShadow(false);
                }
            } else {
                imageView.setProgressVisible(false);
                imageView.showShadow(false);
            }
        }
    }

}
