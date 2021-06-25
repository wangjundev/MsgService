package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class CategoryList {
    @JSONField(name="category-entry")
    private List<String> category_entry;

    public List<String> getCategory_entry() {
        return category_entry;
    }

    public void setCategory_entry(List<String> category_entry) {
        this.category_entry = category_entry;
    }
}
