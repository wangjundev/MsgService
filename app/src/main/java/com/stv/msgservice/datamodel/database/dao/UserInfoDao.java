package com.stv.msgservice.datamodel.database.dao;

import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserInfoDao {
    @Query("SELECT * FROM userinfos")
    LiveData<List<UserInfoEntity>> getUsers();

    @Query("select * from userinfos where uri = :uri")
    LiveData<UserInfoEntity> getUser(String uri);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfo(UserInfoEntity userInfoEntity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateUserInfo(UserInfoEntity userInfoEntity);

    @Query("UPDATE userinfos SET is_attentioned = :isAttentioned WHERE uri = :chatbotId")
    void updateAttentionByChatbotId(String chatbotId, int isAttentioned);

    //使用内连接查询
    @Query("SELECT user_id, domain, uri, expiry_time, etag, json, name, menu, portrait, category, description, last_used_time, pcc_type, verification_signatures, is_attentioned FROM userinfos INNER JOIN conversations ON userinfos.uri = conversations.sender_address WHERE conversations.id = :conversationId")
    LiveData<UserInfoEntity> getUserInfoByConversationId(final long conversationId);

    @Query("SELECT * FROM userinfos ORDER BY last_used_time DESC")
    LiveData<List<UserInfoEntity>> getLatestUsedChatbotList();

    @Query("SELECT * FROM userinfos WHERE is_attentioned = 1")
    LiveData<List<UserInfoEntity>> getAttentionedChatbotList();
}
