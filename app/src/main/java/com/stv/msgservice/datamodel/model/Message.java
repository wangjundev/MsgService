package com.stv.msgservice.datamodel.model;

import com.stv.msgservice.third.activity.LocationData;

public interface Message {
    int getMessageStatus();
    long getId();
    long getTime();
    int getMessageType();
    int getDirection();
    String getThumbnailPath();
    String getAttachmentPath();
    String getContent();
    long getConversationId();
    int getRead();
    LocationData getLocationData();
    String getConversationID();
    void setConversationID(String conversationID);
    String getContributionID();
    void setContributionID(String contributionID);
    String getMessageId();
    void setMessageId(String messageId);
    String getDeliveryStatus();
    void setDeliveryStatus(String deliveryStatus);
    void setAttachmentType(String attachmentType);
    String getAttachmentType();
    String getDraftContent();
    String getSenderAddress();
    void setSenderAddress(String senderAddress);
//    int getPercent();
//    void setPercent(int percent);
}
