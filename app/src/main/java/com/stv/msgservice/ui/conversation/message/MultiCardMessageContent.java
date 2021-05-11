package com.stv.msgservice.ui.conversation.message;

import android.os.Parcel;

import com.stv.msgservice.annotation.ContentTag;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;

@ContentTag(type = MessageConstants.CONTENT_TYPE_MULTI_CARD)
public class MultiCardMessageContent extends MessageContent {
    private String content;
    // 引用信息
//    private QuoteInfo quoteInfo;

    public MultiCardMessageContent() {
    }

    public MultiCardMessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

//    public QuoteInfo getQuoteInfo() {
//        return quoteInfo;
//    }
//
//    public void setQuoteInfo(QuoteInfo quoteInfo) {
//        this.quoteInfo = quoteInfo;
//    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.searchableContent = content;
        payload.mentionedType = mentionedType;
        payload.mentionedTargets = mentionedTargets;
//        if (quoteInfo != null) {
//            JSONObject object = new JSONObject();
//            try {
//                object.put("quote", quoteInfo.encode());
//                payload.binaryContent = object.toString().getBytes();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        content = payload.searchableContent;
        mentionedType = payload.mentionedType;
        mentionedTargets = payload.mentionedTargets;
//        if (payload.binaryContent != null && payload.binaryContent.length > 0) {
//            try {
//                JSONObject object = new JSONObject(new String(payload.binaryContent));
//                quoteInfo = new QuoteInfo();
//                quoteInfo.decode(object.optJSONObject("quote"));
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @Override
    public String digest(Message message) {
        return content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.content);
//        dest.writeParcelable(this.quoteInfo, flags);
    }

    protected MultiCardMessageContent(Parcel in) {
        super(in);
        this.content = in.readString();
//        this.quoteInfo = in.readParcelable(QuoteInfo.class.getClassLoader());
    }

    public static final Creator<MultiCardMessageContent> CREATOR = new Creator<MultiCardMessageContent>() {
        @Override
        public MultiCardMessageContent createFromParcel(Parcel source) {
            return new MultiCardMessageContent(source);
        }

        @Override
        public MultiCardMessageContent[] newArray(int size) {
            return new MultiCardMessageContent[size];
        }
    };
}
