package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class UriEntry {
    @JSONField(name = "addr-uri-type")
    private String addr_uri_type;
    @JSONField(name = "addr-uri")
    private String addr_uri;
    private String label;

    public String getAddr_uri_type() {
        return addr_uri_type;
    }

    public void setAddr_uri_type(String addr_uri_type) {
        this.addr_uri_type = addr_uri_type;
    }

    public String getAddr_uri() {
        return addr_uri;
    }

    public void setAddr_uri(String addr_uri) {
        this.addr_uri = addr_uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
