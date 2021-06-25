package com.stv.msgservice.ui.conversation.message;

import android.os.Parcel;

import com.stv.msgservice.annotation.ContentTag;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;

@ContentTag(type = MessageConstants.CONTENT_TYPE_TEXT_WITH_SUGGESTION)
public class TextSuggestionMessageContent extends MessageContent {
    private String content;

    public TextSuggestionMessageContent() {
    }

    public TextSuggestionMessageContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.searchableContent = content;
        payload.mentionedType = mentionedType;
        payload.mentionedTargets = mentionedTargets;
        return payload;
    }


    @Override
    public void decode(MessagePayload payload) {
        content = payload.searchableContent;
        mentionedType = payload.mentionedType;
        mentionedTargets = payload.mentionedTargets;
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
    }

    protected TextSuggestionMessageContent(Parcel in) {
        super(in);
        this.content = in.readString();
    }

    public static final Creator<TextSuggestionMessageContent> CREATOR = new Creator<TextSuggestionMessageContent>() {
        @Override
        public TextSuggestionMessageContent createFromParcel(Parcel source) {
            return new TextSuggestionMessageContent(source);
        }

        @Override
        public TextSuggestionMessageContent[] newArray(int size) {
            return new TextSuggestionMessageContent[size];
        }
    };
}
