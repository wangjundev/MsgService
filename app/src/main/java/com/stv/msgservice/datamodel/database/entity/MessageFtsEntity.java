package com.stv.msgservice.datamodel.database.entity;

import androidx.room.Entity;
import androidx.room.Fts4;

@Entity(tableName = "messageFts")
@Fts4(contentEntity = MessageEntity.class)
public class MessageFtsEntity {
    private String content;

    public MessageFtsEntity(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
