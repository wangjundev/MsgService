package com.stv.msgservice.datamodel.database.dao;

import com.stv.msgservice.datamodel.database.entity.MessageEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages where id = :messageId")
    LiveData<MessageEntity> getMessage(int messageId);

    @Query("SELECT * FROM messages where conversation_id = :conversationId ORDER BY id ASC")
    LiveData<List<MessageEntity>> getMessages(long conversationId);

    @Query("SELECT * FROM messages where conversation_id = :conversationId AND read = 1")
    LiveData<List<MessageEntity>> getUnReadMessages(long conversationId);

//    @Query("SELECT * FROM messages where conversation_id = :conversationId ORDER BY id DESC")
//    DataSource.Factory<Integer, MessageEntity> getPageMessages(long conversationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertMessage(MessageEntity message);

    @Query("SELECT * FROM messages where conversation_id = :conversationId ORDER BY id DESC LIMIT 1")
    LiveData<MessageEntity> getLastMessage(long conversationId);

    @Query("DELETE FROM messages WHERE id = :messageId")
    void deleteMessage(long messageId);

    @Delete
    void deleteMessage(MessageEntity me);

    @Delete
    void deleteMessages(List<MessageEntity> messageEntityList);

    @Query("SELECT * FROM messages where conversation_id = :conversationId AND id < :fromMessageId ORDER BY id DESC LIMIT 20")
    public LiveData<List<MessageEntity>> loadOldMessages(long conversationId, long fromMessageId);

    @Query("SELECT * FROM messages where conversation_id = :conversationId AND id > :fromMessageId ORDER BY id DESC LIMIT 20")
    public LiveData<List<MessageEntity>> loadNewMessages(long conversationId, long fromMessageId);

    @Query("SELECT count(*) FROM messages where conversation_id = :conversationId AND read = 1")
    public int getUnreadCount(long conversationId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateMessage(MessageEntity me);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateMessagesReadStatus(List<MessageEntity> list);
}
