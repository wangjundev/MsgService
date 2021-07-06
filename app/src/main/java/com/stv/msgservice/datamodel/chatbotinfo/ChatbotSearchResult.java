package com.stv.msgservice.datamodel.chatbotinfo;

import java.util.ArrayList;

public class ChatbotSearchResult {
    ArrayList<SearchedBot> bots;
    int itemsReturned;
    int startIndex;
    int totalItems;

    public ArrayList<SearchedBot> getBots() {
        return bots;
    }

    public int getItemsReturned() {
        return itemsReturned;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getTotalItems() {
        return totalItems;
    }
}
