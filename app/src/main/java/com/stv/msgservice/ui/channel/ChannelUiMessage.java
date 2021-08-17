package com.stv.msgservice.ui.channel;

import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;

public class ChannelUiMessage {
    public boolean isChecked;
    public MessageUserInfoEntity message;

    public ChannelUiMessage(boolean isChecked, MessageUserInfoEntity message) {
        this.isChecked = isChecked;
        this.message = message;
    }
}
