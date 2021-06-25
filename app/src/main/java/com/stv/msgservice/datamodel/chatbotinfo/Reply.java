package com.stv.msgservice.datamodel.chatbotinfo;

public class Reply {
    private String displayText;
    private Postback postback;

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Postback getPostback() {
        return postback;
    }

    public void setPostback(Postback postback) {
        this.postback = postback;
    }
}
