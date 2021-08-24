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
    String getConversationUUID();
    void setConversationUUID(String conversationUUID);
    String getContributionUUID();
    void setContributionUUID(String contributionUUID);
    String getMessageId();
    void setMessageId(String messageId);
    String getDeliveryStatus();
    void setDeliveryStatus(String deliveryStatus);
    void setAttachmentType(String attachmentType);
    String getAttachmentType();
    String getDraftContent();
    String getSenderAddress();
    void setSenderAddress(String senderAddress);
    String getDestinationAddress();
    void setDestinationAddress(String destinationAddress);
    int isFavorited();
    void setIsFavorited(int favorited);
    long getFavoritedTimestamp();
    void setFavoritedTimestamp(long favoritedTimestamp);
//    int getPercent();
//    void setPercent(int percent);
}
