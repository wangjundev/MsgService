package com.stv.msgservice.ui.conversation.message;

import android.os.Parcel;

import com.stv.msgservice.annotation.ContentTag;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

@ContentTag(type = MessageConstants.CONTENT_TYPE_AUDIO)
public class SoundMessageContent extends MediaMessageContent {
    private int duration;

    public SoundMessageContent() {
        this.mediaType = MessageContentMediaType.VOICE;
    }

    public SoundMessageContent(String audioPath) {
        this.localPath = audioPath;
        this.mediaType = MessageContentMediaType.VOICE;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.searchableContent = "[语音]";

        try {
            JSONObject objWrite = new JSONObject();
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
        try {
            if (payload.content != null) {
                JSONObject jsonObject = new JSONObject(payload.content);
                duration = jsonObject.optInt("duration");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String digest(Message message) {
        return "[语音]";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.duration);
    }

    protected SoundMessageContent(Parcel in) {
        super(in);
        this.duration = in.readInt();
    }

    public static final Creator<SoundMessageContent> CREATOR = new Creator<SoundMessageContent>() {
        @Override
        public SoundMessageContent createFromParcel(Parcel source) {
            return new SoundMessageContent(source);
        }

        @Override
        public SoundMessageContent[] newArray(int size) {
            return new SoundMessageContent[size];
        }
    };
}
