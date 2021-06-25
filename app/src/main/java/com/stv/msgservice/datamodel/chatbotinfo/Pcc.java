package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class Pcc {
    @JSONField(name="pcc-type")
    private String pcc_type;
    @JSONField(name="org-details")
    private OrgDetails org_details;

    public String getPcc_type() {
        return pcc_type;
    }

    public void setPcc_type(String pcc_type) {
        this.pcc_type = pcc_type;
    }

    public OrgDetails getOrg_details() {
        return org_details;
    }

    public void setOrg_details(OrgDetails org_details) {
        this.org_details = org_details;
    }
}
