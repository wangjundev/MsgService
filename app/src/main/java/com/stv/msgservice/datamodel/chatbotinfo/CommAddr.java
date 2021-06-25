package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class CommAddr {
    @JSONField(name="uri-entry")
    private List<UriEntry> uri_entry;
    private Tel tel;

    public List<UriEntry> getUri_entry() {
        return uri_entry;
    }

    public void setUri_entry(List<UriEntry> uri_entry) {
        this.uri_entry = uri_entry;
    }

    public Tel getTel() {
        return tel;
    }

    public void setTel(Tel tel) {
        this.tel = tel;
    }
}
