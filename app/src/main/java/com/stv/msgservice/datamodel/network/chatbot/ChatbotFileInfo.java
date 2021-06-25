package com.stv.msgservice.datamodel.network.chatbot;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "file-info", strict = false)
public class ChatbotFileInfo {
    @Attribute(name="type", required = false)
    private String type;
    @Element(name="file-size", required = false)
    private long file_size;
    @Element(name="file-name", required = false)
    private String file_name;
    @Element(name="content-type", required = false)
    private String content_type;
    @Element(name="data", required = false)
    private ChatbotFileData data;
    public void setType(String type) {
        this.type = type;
    }
    public String geType() {
        return type;
    }

    public void setFileSize(long file_size) {
        this.file_size = file_size;
    }
    public long getFileSize() {
        return file_size;
    }

    public void setContentType(String content_type) {
        this.content_type = content_type;
    }
    public String getContent_type() {
        return content_type;
    }

    public void setData(ChatbotFileData data) {
        this.data = data;
    }
    public ChatbotFileData getData() {
        return data;
    }

    public String getFileName() {
        return file_name;
    }

    public void setFileName(String file_name) {
        this.file_name = file_name;
    }

    public String getType() {
        return type;
    }
}
