package com.stv.msgservice.ui.conversation.message.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.stv.msgservice.R2;
import com.stv.msgservice.annotation.EnableContextMenu;
import com.stv.msgservice.annotation.MessageContentType;
import com.stv.msgservice.annotation.MessageContextMenuItem;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.WebViewNewsActivity;
import com.stv.msgservice.ui.conversation.ConversationFragment;
import com.stv.msgservice.ui.conversation.message.ImageMessageContent;
import com.stv.msgservice.ui.widget.BubbleImageView;
import com.stv.msgservice.utils.WeChatImageUtils;

import java.util.Hashtable;

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
    private boolean isQrImage;
    private Result ret;

    public ImageMessageContentViewHolder(ConversationFragment fragment, RecyclerView.Adapter adapter, View itemView) {
        super(fragment, adapter, itemView);
    }

    @Override
    public void onBind(Message message) {
        Bitmap thumbnail = BitmapFactory.decodeFile(message.getThumbnailPath());
//        Bitmap image = BitmapFactory.decodeFile(message.getAttachmentPath());
//        int imageSize[] = WeChatImageUtils.getImageSizeByOrgSizeToWeChat((int) image.getWidth(), (int) image.getHeight());
        int imageSize[] = WeChatImageUtils.getImageSizeByOrgSizeToWeChat((int) thumbnail.getWidth(), (int) thumbnail.getHeight());
        int width = imageSize[0] > 0 ? imageSize[0] : 200;
        int height = imageSize[1] > 0 ? imageSize[1] : 200;
        imageView.getLayoutParams().width = width;
        imageView.getLayoutParams().height = height;
//        if (FileUtils.isFileExists(imageMessage.localPath)) {
//            imagePath = imageMessage.localPath;
//        } else {
//            imagePath = imageMessage.remoteUrl;
//        }
        imagePath = message.getAttachmentPath();
        loadMedia(thumbnail, imagePath, imageView);
//        loadImage(message.getThumbnailPath(), message.getAttachmentPath(), imageView);
    }

    public static Result parsePic(Bitmap bitmap) {
        // 解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        // 新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(lWidth,
                lHeight, lPixels);
        // 将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                rgbLuminanceSource));
        // 初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        // 开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @OnClick(R2.id.imageView)
    void preview() {
        Log.i("Junwang", "onClick time="+ SystemClock.currentThreadTimeMillis());
        previewMM();

//        BitmapDrawable drawable = (BitmapDrawable)imageView.getDrawable();
//        if(drawable == null)
//        {
//            Log.i("Junwang", "二维码不能解析成BitmapDrawable");
//            Toast.makeText(fragment.getContext(), "二维码不能解析成BitmapDrawable",
//                Toast.LENGTH_LONG).show();
//        }
//        Bitmap bitmap = drawable.getBitmap();
//        Result ret = parsePic(bitmap);
//        if (null == ret) {
//            Log.i("Junwang", "解析结果：null");
//            Toast.makeText(fragment.getContext(), "解析结果：null",
//                    Toast.LENGTH_LONG).show();
//        } else {
//            LogUtil.i("Junwang", "qrcode="+ret.toString());
//            WebViewNewsActivity.start(fragment.getContext(), ret.toString());
//            Toast.makeText(fragment.getContext(),
//                    "解析结果：" + ret.toString(), Toast.LENGTH_LONG).show();
//        }
    }

    @MessageContextMenuItem(tag = MessageContextMenuItemTags.TAG_QR, confirm = false, priority = 12)
    public void parseQRCode(View itemView, Message message) {
        if (null == ret) {

        } else {
            LogUtil.i("Junwang", "qrcode="+ret.toString());
            WebViewNewsActivity.start(fragment.getContext(), ret.toString());
        }
    }

    @Override
    public String contextMenuTitle(Context context, String tag) {
        Bitmap bitmap = BitmapFactory.decodeFile(message.getThumbnailPath());
        if(bitmap != null) {
            ret = parsePic(bitmap);
            if(ret != null){
                Log.i("Junwang", "ret="+ret);
                isQrImage = true;
            }else{
                isQrImage = false;
            }
        }else{
            Log.i("Junwang", "bitmap is null");
            isQrImage = false;
        }
        if (isQrImage && MessageContextMenuItemTags.TAG_QR.equals(tag)) {
            return "识别二维码";
        }
        return super.contextMenuTitle(context, tag);
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
