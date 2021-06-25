package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class Tel {
    @JSONField(name="tel-type")
    private String tel_type;
    @JSONField(name="tel-nb")
    private TelNb tel_nb;
    private String label;

    public String getTel_type() {
        return tel_type;
    }

    public void setTel_type(String tel_type) {
        this.tel_type = tel_type;
    }

    public TelNb getTel_nb() {
        return tel_nb;
    }

    public void setTel_nb(TelNb tel_nb) {
        this.tel_nb = tel_nb;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
