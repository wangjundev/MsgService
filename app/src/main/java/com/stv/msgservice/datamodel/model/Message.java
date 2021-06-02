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
}
