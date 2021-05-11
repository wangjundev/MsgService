package com.stv.msgservice.datamodel.network.chatbot;

import java.util.List;

public class CardLayout {
    String cardOrientation;
    String imageAlignment;
    String cardWidth;
    List<String> titleFontStyle;
    List<String> descriptionFontStyle;
    String style;

    public String getCardOrientation() {
        return cardOrientation;
    }

    public String getImageAlignment() {
        return imageAlignment;
    }

    public String getCardWidth() {
        return cardWidth;
    }
}
