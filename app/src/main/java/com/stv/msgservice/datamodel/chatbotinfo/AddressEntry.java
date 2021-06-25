package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class AddressEntry {
    @JSONField(name="addr-string")
    private String addr_string;
    private String label;

    public String getAddr_string() {
        return addr_string;
    }

    public void setAddr_string(String addr_string) {
        this.addr_string = addr_string;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
