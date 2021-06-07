package com.stv.msgservice.datamodel.model;

public interface Conversation{
    long getId();
    long getLastTimestamp();
    String getSnippetText();
    String getNormalizedDestination();
    int getLatestMessageStatus();
    long getLatestMessageId();
    int getUnreadCount();
    void setUnreadCount(int unreadCount);
    void setLatestMessageId(long latestMessageId);
    void setTop(boolean top);
    boolean isTop();
    void setTopTimestamp(long topTimestamp);
}
