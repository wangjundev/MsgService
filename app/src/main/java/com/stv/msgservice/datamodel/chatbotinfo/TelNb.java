package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class TelNb {
    @JSONField(name="tel-str")
    private String tel_str;

    public String getTel_str() {
        return tel_str;
    }

    public void setTel_str(String tel_str) {
        this.tel_str = tel_str;
    }
}
