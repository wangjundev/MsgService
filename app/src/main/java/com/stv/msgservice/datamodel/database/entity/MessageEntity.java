package com.stv.msgservice.datamodel.database.entity;

import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_AUDIO;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_IMAGE;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_MULTI_CARD;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_SINGLE_CARD;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_TEXT;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_VIDEO;


@Entity(tableName = "messages", indices = {@Index("conversation_id"), @Index("message_status")})
public class MessageEntity implements Message {
    @PrimaryKey(autoGenerate = true)
    private long id;

    /* conversation id that this message belongs to */
    @ColumnInfo(name = "conversation_id")
    private long conversationId;

    /* This is bugle's internal status for the message */
    @ColumnInfo(name = "message_status")
    private int messageStatus;

    /* This is the time that the sender sent the message */
    @ColumnInfo(name = "sent_timestamp")
    private long sentTimestamp;

    /* Time that we received the message on this device */
    @ColumnInfo(name = "received_timestamp")
    private long receivedTimeStamp;

    private String content;

    private int read;

    @ColumnInfo(name = "draft_content")
    private String draftContent;

    @ColumnInfo(name = "message_type")
    private int messageType;

    @ColumnInfo(name = "attachment_path")
    private String attachmentPath;

    @ColumnInfo(name = "thumbnail_path")
    private String thumbnailPath;

    private int direction;

    public long getId() {
        return id;
    }

    public int getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(int messageStatus) {
        this.messageStatus = messageStatus;
    }

    public long getSentTimestamp() {
        return sentTimestamp;
    }

    public void setSentTimestamp(long sentTimestamp) {
        this.sentTimestamp = sentTimestamp;
    }

    public long getReceivedTimeStamp() {
        return receivedTimeStamp;
    }

    public void setReceivedTimeStamp(long receivedTimeStamp) {
        this.receivedTimeStamp = receivedTimeStamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDraftContent() {
        return draftContent;
    }

    public void setDraftContent(String draftContent) {
        this.draftContent = draftContent;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String generateSnippetText(){
        switch (getMessageType()){
            case CONTENT_TYPE_TEXT:
                return getContent();
            case CONTENT_TYPE_VIDEO:
                return "[视频]";
            case CONTENT_TYPE_AUDIO:
                return "[音频]";
            case CONTENT_TYPE_IMAGE:
                return "[图片]";
            case CONTENT_TYPE_MULTI_CARD:
                return "[多卡片]";
            case CONTENT_TYPE_SINGLE_CARD:
                return "[单卡片]";
            default:
                return getContent();
        }
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public long getTime(){
        return getMessageStatus() == MessageConstants.BUGLE_STATUS_INCOMING_COMPLETE ? receivedTimeStamp : sentTimestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MessageEntity)) return false;
        MessageEntity that = (MessageEntity) obj;
        return (getId() == that.getId()) && (getContent().equals(that.getContent()));
    }
}