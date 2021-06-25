package com.stv.msgservice.datamodel.network.chatbot;

import org.simpleframework.xml.Attribute;

public class ChatbotFileData {
    @Attribute(name="url", required = false)
    private String url;
    @Attribute(name="until", required = false)
    private /*Date*/String until;
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }

    public void setUntil(String until) {
        this.until = until;
    }
    public String getUntil() {
        return until;
    }
}
