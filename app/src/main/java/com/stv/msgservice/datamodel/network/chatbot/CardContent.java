package com.stv.msgservice.datamodel.network.chatbot;

public class CardContent {
    ChatbotContentMedia media;
    String title;
    String description;
    int cardType;

    SuggestionActionWrapper[] suggestions;

    public ChatbotContentMedia getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SuggestionActionWrapper[] getSuggestionActionWrapper() {
        return suggestions;
    }

    public int getCardType() {
        return cardType;
    }
}
