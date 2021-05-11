package com.stv.msgservice.datamodel.database.dao;

import com.stv.msgservice.datamodel.database.entity.ConversationEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface ConversationDao {
    @Query("SELECT * FROM conversations")
    LiveData<List<ConversationEntity>> getConversations();

    @Query("SELECT id FROM conversations WHERE conversations.participant_normalized_destination = :contactNumber ")
    long getConversationId(String contactNumber);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ConversationEntity> conversations);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertConversation(ConversationEntity conversations);

    @Update
    void updateConversation(ConversationEntity ce);

    @Query("select * from conversations where id = :conversationId")
    LiveData<ConversationEntity> loadConversation(int conversationId);

    @Query("DELETE FROM conversations WHERE id = :conversationId")
    void deleteConversation(long conversationId);

    @Delete
    void deleteConversation(ConversationEntity ce);
}
