package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class Address {
    @JSONField(name="address-entry")
    private List<AddressEntry> address_entry;

    public List<AddressEntry> getAddress_entry() {
        return address_entry;
    }

    public void setAddress_entry(List<AddressEntry> address_entry) {
        this.address_entry = address_entry;
    }
}
