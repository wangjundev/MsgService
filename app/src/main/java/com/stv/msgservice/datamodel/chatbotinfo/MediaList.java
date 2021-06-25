package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class MediaList {
    @JSONField(name="media-entry")
    private List<MediaEntry> media_entry;

    public List<MediaEntry> getMedia_entry() {
        return media_entry;
    }

    public void setMedia_entry(List<MediaEntry> media_entry) {
        this.media_entry = media_entry;
    }
}
