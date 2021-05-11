package com.stv.msgservice.datamodel.network.chatbot;

public class ChatbotMultiCard {
    private int code;
    private String message;
    MultiCardChatbotMsg data;

    public MultiCardChatbotMsg getdata() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
