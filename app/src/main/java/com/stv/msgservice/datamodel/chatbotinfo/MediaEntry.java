package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class MediaEntry {
    @JSONField(name="media-content")
    private String media_content;
    private Media media;
    private String label;

    public String getMedia_content() {
        return media_content;
    }

    public void setMedia_content(String media_content) {
        this.media_content = media_content;
    }

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
