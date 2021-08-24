package com.stv.msgservice.datamodel.database.dao;

import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;

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
    LiveData<MessageEntity> getMessage(long messageId);

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

    @Query("SELECT * FROM messages where conversation_id = :conversationId AND id < :fromMessageId ORDER BY id ASC LIMIT 20")
    public LiveData<List<MessageEntity>> loadOldMessages(long conversationId, long fromMessageId);

    @Query("SELECT * FROM messages where conversation_id = :conversationId AND id > :fromMessageId ORDER BY id DESC LIMIT 20")
    public LiveData<List<MessageEntity>> loadNewMessages(long conversationId, long fromMessageId);

    @Query("SELECT count(*) FROM messages where conversation_id = :conversationId AND read = 1")
    public int getUnreadCount(long conversationId);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateMessage(MessageEntity me);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateMessagesReadStatus(List<MessageEntity> list);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public void updateMessageSendStatus(MessageEntity me);

//    @Query("SELECT messages.* FROM messages JOIN messageFts ON (messages.id = messageFts.rowid) "
//            + "WHERE messageFts MATCH :query")
    @Query("SELECT messages.* FROM messages WHERE messages.content LIKE :query AND conversation_id = :conversationId AND message_type = 200")
    List<MessageEntity> searchMessages(long conversationId, String query);

    @Query("SELECT messages.* FROM messages WHERE messages.content LIKE :query AND message_type = 200")
    List<MessageEntity> searchAllMessages(String query);

    @Query("SELECT messages.*, name, portrait, description, verification_signatures, is_attentioned FROM messages INNER JOIN userinfos ON messages.sender_address = userinfos.uri  ORDER BY id DESC")
    LiveData<List<MessageUserInfoEntity>> getAllMessages();

    @Query("SELECT messages.*, name, portrait, description, verification_signatures, is_attentioned FROM messages INNER JOIN userinfos ON messages.sender_address = userinfos.uri  WHERE messages.is_favorited = 1 ORDER BY id DESC")
    LiveData<List<MessageUserInfoEntity>> getFavoritedMessages();

    @Query("UPDATE messages SET is_favorited = :isFavorited, favorited_timestamp = :favoritedTimestamp WHERE id = :messageId")
    void updateMessageFavoriteStatusById(long messageId, int isFavorited, long favoritedTimestamp);
}
