package com.stv.msgservice.datamodel.model;

public interface Conversation{
    long getId();
    long getLastTimestamp();
    String getSnippetText();
    String getNormalizedDestination();
    int getLatestMessageStatus();
    long getLatestMessageId();
}
