package com.stv.msgservice.datamodel.chatbotinfo;

public class ChatbotSearchResult {
    SearchedBot[] bots;
    int itemsReturned;
    int startIndex;
    int totalItems;

    public SearchedBot[] getBots() {
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
