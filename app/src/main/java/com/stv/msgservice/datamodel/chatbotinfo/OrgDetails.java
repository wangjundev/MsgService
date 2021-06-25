package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class OrgDetails {
    @JSONField(name="org-name")
    private List<OrgName> org_name;
    @JSONField(name="comm-addr")
    private CommAddr comm_addr;
    @JSONField(name="media-list")
    private MediaList media_list;
    @JSONField(name="category-list")
    private CategoryList category_list;
    @JSONField(name="org-description")
    private String org_description;

    public List<OrgName> getOrg_name() {
        return org_name;
    }

    public void setOrg_name(List<OrgName> org_name) {
        this.org_name = org_name;
    }

    public CommAddr getComm_addr() {
        return comm_addr;
    }

    public void setComm_addr(CommAddr comm_addr) {
        this.comm_addr = comm_addr;
    }

    public MediaList getMedia_list() {
        return media_list;
    }

    public void setMedia_list(MediaList media_list) {
        this.media_list = media_list;
    }

    public CategoryList getCategory_list() {
        return category_list;
    }

    public void setCategory_list(CategoryList category_list) {
        this.category_list = category_list;
    }

    public String getOrg_description() {
        return org_description;
    }

    public void setOrg_description(String org_description) {
        this.org_description = org_description;
    }
}
