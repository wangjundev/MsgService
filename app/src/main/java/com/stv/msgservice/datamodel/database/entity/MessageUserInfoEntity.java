package com.stv.msgservice.datamodel.database.entity;

import com.stv.msgservice.third.activity.LocationData;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

public class MessageUserInfoEntity{
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

    private String name;
    private String portrait;
    @ColumnInfo(name = "verification_signatures")
    private String verificationSignatures;
    @ColumnInfo(name = "is_attentioned")
    private int isAttentioned;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
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

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
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

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
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

    public int getIsFavorited() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getVerificationSignatures() {
        return verificationSignatures;
    }

    public void setVerificationSignatures(String verificationSignatures) {
        this.verificationSignatures = verificationSignatures;
    }

    public int isAttentioned() {
        return isAttentioned;
    }

    public void setIsAttentioned(int isAttentioned) {
        this.isAttentioned = isAttentioned;
    }
}
