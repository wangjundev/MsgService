package com.stv.msgservice.datamodel.database.dao;

import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserInfoDao {
    @Query("SELECT * FROM userinfos")
    LiveData<List<UserInfoEntity>> getUsers();

    @Query("select * from userinfos where uri = :uri")
    LiveData<UserInfoEntity> getUser(String uri);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserInfo(UserInfoEntity userInfoEntity);

    //使用内连接查询
    @Query("SELECT user_id, domain, uri, expiry_time, etag, json, name, menu, portrait FROM userinfos INNER JOIN conversations ON userinfos.uri = conversations.participant_normalized_destination WHERE conversations.id = :conversationId")
    LiveData<UserInfoEntity> getUserInfoByConversationId(final long conversationId);
}
