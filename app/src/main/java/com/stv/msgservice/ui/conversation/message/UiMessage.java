package com.stv.msgservice.ui.conversation.message;

import com.stv.msgservice.datamodel.database.entity.MessageEntity;

public class UiMessage {
    public boolean isChecked;
    public MessageEntity message;

    public UiMessage(boolean isChecked, MessageEntity message) {
        this.isChecked = isChecked;
        this.message = message;
    }
}
