package com.stv.msgservice.datamodel.chatbotinfo;

import java.util.List;

public class Menu {
    private List<Entries> entries;
    public void setEntries(List<Entries> entries) {
        this.entries = entries;
    }
    public List<Entries> getEntries() {
        return entries;
    }
}
