package com.stv.msgservice.ui.conversation.message;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.stv.msgservice.annotation.ContentTag;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.multimedia.VideoParam;
import com.stv.msgservice.utils.WeChatImageUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @refactor dhl
 * 添加小视频的宽高，时长
 */

@ContentTag(type = MessageConstants.CONTENT_TYPE_VIDEO)
public class VideoMessageContent extends MediaMessageContent {

    private static final String TAG = "VideoMessageContent";
    private Bitmap thumbnail;
    private byte[] thumbnailBytes;

    private long duration ;

    // 所有消息都需要一个默认构造函数
    public VideoMessageContent() {
        this.mediaType = MessageContentMediaType.VIDEO;
    }

    public VideoMessageContent(String videoPath) {
        this.localPath = videoPath;
        this.mediaType = MessageContentMediaType.VIDEO;
        if (!TextUtils.isEmpty(localPath)) {
            VideoParam videoParam = WeChatImageUtils.getVideoParam(localPath);
            duration = videoParam.getDuration();
            thumbnailBytes = videoParam.getThumbnailBytes();
        }
    }

    public Bitmap getThumbnail() {
        if (thumbnail != null) {
            return thumbnail;
        }
        if (thumbnailBytes != null) {
            thumbnail = BitmapFactory.decodeByteArray(thumbnailBytes, 0, thumbnailBytes.length);
        } else {
            if (!TextUtils.isEmpty(localPath)) {
                thumbnail = ThumbnailUtils.createVideoThumbnail(localPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, 320, 240,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            }
        }
        return thumbnail;
    }

    public void setThumbnailBytes(byte[] thumbnailBytes) {
        this.thumbnailBytes = thumbnailBytes;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.searchableContent = "[视频]";
     /* //  if (thumbnailBytes == null && !TextUtils.isEmpty(localPath)) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(localPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//              thumbnail = ThumbnailUtils.extractThumbnail(thumbnail, 320, 240, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                payload.binaryContent = baos.toByteArray();
                Log.e(TAG,"binaryContent="+ payload.binaryContent.length/1024+"kb");
            } catch (Exception e) {
                e.printStackTrace();
            }
       *//* } else {
            payload.binaryContent = thumbnailBytes;
        }*/
        payload.binaryContent = thumbnailBytes;
        try {
            JSONObject objWrite = new JSONObject();
            objWrite.put("d", duration);
            objWrite.put("duration", duration);
            payload.content = objWrite.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        thumbnailBytes = payload.binaryContent;
        try {
            JSONObject jsonObject = new JSONObject(payload.content);
            if(jsonObject.has("d")) {
                duration = jsonObject.optLong("d");
            } else {
                duration = jsonObject.optLong("duration");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    @Override
    public String digest(Message message) {
        return "[视频]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeByteArray(this.thumbnailBytes);
        dest.writeLong(this.duration);
    }

    public long getDuration() {
        return duration;
    }

    protected VideoMessageContent(Parcel in) {
        super(in);
        this.thumbnailBytes = in.createByteArray();
        this.duration = in.readLong();
    }

    public static final Creator<VideoMessageContent> CREATOR = new Creator<VideoMessageContent>() {
        @Override
        public VideoMessageContent createFromParcel(Parcel source) {
            return new VideoMessageContent(source);
        }

        @Override
        public VideoMessageContent[] newArray(int size) {
            return new VideoMessageContent[size];
        }
    };



}
