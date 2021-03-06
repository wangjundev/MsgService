package com.stv.msgservice.datamodel.database.entity;

import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.third.activity.LocationData;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_AUDIO;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_IMAGE;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_LOCATION;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_MULTI_CARD;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_MULTI_CARD_WITH_SUGGESTION;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_SINGLE_CARD;
import static com.stv.msgservice.datamodel.constants.MessageConstants.CONTENT_TYPE_SINGLE_CARD_WITH_SUGGESTION;
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

    @ColumnInfo(name = "attachment_type")
    private String attachmentType;

    @ColumnInfo(name = "thumbnail_path")
    private String thumbnailPath;

    private int direction;

    private LocationData locationData;

    @ColumnInfo(name = "conversation_uuid")
    private String conversationUUID;

    @ColumnInfo(name = "contribution_uuid")
    private String contributionUUID;

    @ColumnInfo(name = "message_id")
    private String messageId;

    @ColumnInfo(name = "delivery_status")
    private String deliveryStatus;

    private String domain;

    @ColumnInfo(name = "sender_address")
    private String senderAddress;

    @ColumnInfo(name = "destination_address")
    private String destinationAddress;

    @ColumnInfo(name = "is_favorited")
    private int isFavorited;

    @ColumnInfo(name = "favorited_timestamp")
    private long favoritedTimestamp;

//    private int percent;

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

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
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
                return "[??????]";
            case CONTENT_TYPE_AUDIO:
                return "[??????]";
            case CONTENT_TYPE_IMAGE:
                return "[??????]";
            case CONTENT_TYPE_MULTI_CARD:
            case CONTENT_TYPE_MULTI_CARD_WITH_SUGGESTION:
                return "[?????????]";
            case CONTENT_TYPE_SINGLE_CARD:
            case CONTENT_TYPE_SINGLE_CARD_WITH_SUGGESTION:
                return "[?????????]";
            case CONTENT_TYPE_LOCATION:
                return "[??????]";
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

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }

    public String getConversationUUID() {
        return conversationUUID;
    }

    public void setConversationUUID(String conversationUUID) {
        this.conversationUUID = conversationUUID;
    }

    public String getContributionUUID() {
        return contributionUUID;
    }

    public void setContributionUUID(String contributionUUID) {
        this.contributionUUID = contributionUUID;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    //    public int getPercent() {
//        return percent;
//    }
//
//    public void setPercent(int percent) {
//        this.percent = percent;
//    }


    public int isFavorited() {
        return isFavorited;
    }

    public void setIsFavorited(int isFavorited) {
        this.isFavorited = isFavorited;
    }

    public long getFavoritedTimestamp() {
        return favoritedTimestamp;
    }

    public void setFavoritedTimestamp(long favoritedTimestamp) {
        this.favoritedTimestamp = favoritedTimestamp;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MessageEntity)) return false;
        MessageEntity that = (MessageEntity) obj;
        return (getId() == that.getId()) && (getContent().equals(that.getContent()));
    }

//    public MessageEntity(long id, long conversationId, int messageStatus, long sentTimestamp, long receivedTimeStamp, String content, int read, String draftContent, int messageType, String attachmentPath, String thumbnailPath, int direction, LocationData locationData) {
//        this.id = id;
//        this.conversationId = conversationId;
//        this.messageStatus = messageStatus;
//        this.sentTimestamp = sentTimestamp;
//        this.receivedTimeStamp = receivedTimeStamp;
//        this.content = content;
//        this.read = read;
//        this.draftContent = draftContent;
//        this.messageType = messageType;
//        this.attachmentPath = attachmentPath;
//        this.thumbnailPath = thumbnailPath;
//        this.direction = direction;
//        this.locationData = locationData;
//    }
}
