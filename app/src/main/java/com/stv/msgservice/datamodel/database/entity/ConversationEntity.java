package com.stv.msgservice.datamodel.database.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.stv.msgservice.datamodel.model.Conversation;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "conversations", indices = {@Index(value = "participant_normalized_destination", unique = true)})
public class ConversationEntity  implements Conversation, Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;

    /* Latest Message ID for the read status to display in conversation list */
    @ColumnInfo(name = "latest_message_id")
    private long latestMessageId;

    @ColumnInfo(name = "latest_message_status")
    private int latestMessageStatus;

    /* Latest text snippet for display in conversation list */
    @ColumnInfo(name = "snippet_text")
    private String snippetText;

    /* Latest draft text snippet for display, empty string if none exists */
    @ColumnInfo(name = "draft_snippet_text")
    private String draftSnippetText;

    /* Last read message timestamp */
    @ColumnInfo(name = "last_timestamp")
    private long lastTimestamp;

    @ColumnInfo(name = "participant_normalized_destination")
    private String normalizedDestination;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getLatestMessageId() {
        return latestMessageId;
    }

    public void setLatestMessageId(long latestMessageId) {
        this.latestMessageId = latestMessageId;
    }

    public String getSnippetText() {
        return snippetText;
    }

    public void setSnippetText(String snippetText) {
        this.snippetText = snippetText;
    }

    public String getDraftSnippetText() {
        return draftSnippetText;
    }

    public void setDraftSnippetText(String draftSnippetText) {
        this.draftSnippetText = draftSnippetText;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getNormalizedDestination() {
        return normalizedDestination;
    }

    public void setNormalizedDestination(String normalizedDestination) {
        this.normalizedDestination = normalizedDestination;
    }

    public int getLatestMessageStatus() {
        return latestMessageStatus;
    }

    public void setLatestMessageStatus(int latestMessageStatus) {
        this.latestMessageStatus = latestMessageStatus;
    }

    public static final Parcelable.Creator<ConversationEntity> CREATOR = new Parcelable.Creator<ConversationEntity>() {
        @Override
        public ConversationEntity createFromParcel(Parcel source) {
            return new ConversationEntity(source);
        }

        @Override
        public ConversationEntity[] newArray(int size) {
            return new ConversationEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeString(this.draftSnippetText);
        parcel.writeLong(this.lastTimestamp);
        parcel.writeLong(this.latestMessageId);
        parcel.writeString(this.snippetText);
        parcel.writeInt(this.latestMessageStatus);
        parcel.writeString(this.normalizedDestination);
    }

    public ConversationEntity(Parcel parcel) {
        this.id = parcel.readLong();
        this.draftSnippetText = parcel.readString();
        this.lastTimestamp = parcel.readLong();
        this.latestMessageId = parcel.readLong();
        this.snippetText = parcel.readString();
        this.latestMessageStatus = parcel.readInt();
        this.normalizedDestination = parcel.readString();
    }

    public ConversationEntity() {
    }
}
