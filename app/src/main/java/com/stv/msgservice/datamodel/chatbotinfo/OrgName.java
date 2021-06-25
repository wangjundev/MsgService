package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class OrgName {
    @JSONField(name="display-name")
    private String display_name;
    @JSONField(name="org-name-type")
    private String org_name_type;

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getOrg_name_type() {
        return org_name_type;
    }

    public void setOrg_name_type(String org_name_type) {
        this.org_name_type = org_name_type;
    }
}
