package com.stv.msgservice.datamodel.network.chatbot;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "file", strict = false)
public class ChatbotFile {
    @Attribute(name="xmlns", required = false)
    private String xmlns;
    @ElementList(name="file-info", inline = true)
    private List<ChatbotFileInfo> file_info;
    public void setxmlns(String xmlns) {
        this.xmlns = xmlns;
    }
    public String getxmlns() {
        return xmlns;
    }

    public void setFileInfo(List<ChatbotFileInfo> file_info) {
        this.file_info = file_info;
    }
    public List<ChatbotFileInfo> getFileInfo() {
        return file_info;
    }
}
