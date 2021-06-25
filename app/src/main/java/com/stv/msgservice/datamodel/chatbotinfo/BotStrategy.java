package com.stv.msgservice.datamodel.chatbotinfo;

import com.alibaba.fastjson.annotation.JSONField;

public class BotStrategy {
    @JSONField(name="file-size-limit")
    private String file_size_limit;

    public String getFile_size_limit() {
        return file_size_limit;
    }

    public void setFile_size_limit(String file_size_limit) {
        this.file_size_limit = file_size_limit;
    }
}
