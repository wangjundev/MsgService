package com.stv.msgservice.ui.conversation.message;

import android.os.Parcel;
import android.text.TextUtils;

import com.stv.msgservice.annotation.ContentTag;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.model.Message;

import java.io.File;

@ContentTag(type = MessageConstants.CONTENT_TYPE_FILE)
public class FileMessageContent extends MediaMessageContent {
    private String name;
    private int size;
    private static final String FILE_NAME_PREFIX = "[文件] ";

    public FileMessageContent() {
        this.mediaType = MessageContentMediaType.FILE;
    }

    public FileMessageContent(String filePath) {
        File file = new File(filePath);
        this.name = filePath.substring(filePath.lastIndexOf("/") + 1);
        this.size = (int) file.length();
        this.localPath = filePath;
        this.mediaType = MessageContentMediaType.FILE;
    }

    @Override
    public MessagePayload encode() {
        MessagePayload payload = super.encode();
        payload.searchableContent = FILE_NAME_PREFIX + name;
        payload.content = size + "";

        return payload;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void decode(MessagePayload payload) {
        super.decode(payload);
        if (TextUtils.isEmpty(payload.searchableContent)) {
            return;
        }
        if (payload.searchableContent.startsWith(FILE_NAME_PREFIX)) {
            name = payload.searchableContent.substring(payload.searchableContent.indexOf(FILE_NAME_PREFIX) + FILE_NAME_PREFIX.length());
        } else {
            name = payload.searchableContent;
        }
        size = Integer.parseInt(payload.content);
    }

    @Override
    public String digest(Message message) {
        return "[文件]" + name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeInt(this.size);
    }

    protected FileMessageContent(Parcel in) {
        super(in);
        this.name = in.readString();
        this.size = in.readInt();
    }

    public static final Creator<FileMessageContent> CREATOR = new Creator<FileMessageContent>() {
        @Override
        public FileMessageContent createFromParcel(Parcel source) {
            return new FileMessageContent(source);
        }

        @Override
        public FileMessageContent[] newArray(int size) {
            return new FileMessageContent[size];
        }
    };
}
