package com.stv.msgservice.ui.conversation.message;

public enum MessageContentMediaType {
    GENERAL(0),
    IMAGE(1),
    VOICE(2),
    VIDEO(3),
    FILE(4),
    PORTRAIT(5),
    FAVORITE(6),
    STICKER(7),
    MOMENTS(8);

    private int value;

    MessageContentMediaType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static MessageContentMediaType mediaType(int mediaType) {
        MessageContentMediaType type = null;
        if (mediaType >= 0 && mediaType < MessageContentMediaType.values().length) {
            return MessageContentMediaType.values()[mediaType];
        }
        return null;
        //throw new IllegalArgumentException("mediaType " + mediaType + " is invalid");
    }
}
