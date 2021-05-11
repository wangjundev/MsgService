package com.stv.msgservice.datamodel.network.chatbot;

public class MultiCardChatbotMsg {
    int msgType;
    String plainText;
    GeneralPurposeCardCarousel generalPurposeCardCarousel;

    public GeneralPurposeCardCarousel getGeneralPurposeCardCarousel() {
        return generalPurposeCardCarousel;
    }

    public int getMsgType() {
        return msgType;
    }

    public String getPlainText() {
        return plainText;
    }
}
