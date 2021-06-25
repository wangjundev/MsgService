package com.stv.msgservice.datamodel.network.chatbot;

public class ChatbotMessageBean {
    private String content_type;
    private String content_length;
    private String content_text;

    public ChatbotMessageBean(String content_type, String content_length, String content_text) {
        this.content_type = content_type;
        this.content_length = content_length;
        this.content_text = content_text;
    }

    public void setContent_type(String content_type) {
        this.content_type = content_type;
    }

    public void setContent_length(String content_length) {
        this.content_length = content_length;
    }

    public void setContent_text(String content_text) {
        this.content_text = content_text;
    }

    public String getContent_type() {
        return content_type;
    }

    public String getContent_length() {
        return content_length;
    }

    public String getContent_text() {
        return content_text;
    }
}
