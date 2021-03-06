package com.stv.msgservice.datamodel.datarepository;

import android.util.Log;

import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.database.entity.MessageUserInfoEntity;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class DataRepository {
    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<ConversationEntity>> mObservableConversations;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableConversations = new MediatorLiveData<>();
        loadConversations();
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    public void loadConversations() {
        mObservableConversations.addSource(mDatabase.conversationDao().getConversations(),
                ConversationEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        Log.i("Junwang", "mObservableConversations postValue.");
//                        mObservableConversations.postValue(ConversationEntities);
                    }
                });
    }

    public LiveData<List<ConversationEntity>> getConversations(){
        return mDatabase.conversationDao().getConversations();
    }


    public LiveData<ConversationEntity> loadConversation(final int conversationId) {
        return mDatabase.conversationDao().loadConversation(conversationId);
    }

    public LiveData<List<MessageEntity>> loadOldMessages(long conversationId, long fromMessageId, int count){
        return mDatabase.messageDao().loadOldMessages(conversationId, fromMessageId);
    }

    public LiveData<List<MessageEntity>> loadNewMessages(long conversationId, long fromMessageId, int count){
        return mDatabase.messageDao().loadNewMessages(conversationId, fromMessageId);
    }

    public LiveData<List<MessageEntity>> getMessages(final long conversationId) {
        return mDatabase.messageDao().getMessages(conversationId);
    }

    public List<MessageEntity> searchMessages(long conversationId, String query) {
        return mDatabase.messageDao().searchMessages(conversationId, query);
    }

    public LiveData<List<MessageEntity>> getUnReadMessages(final long conversationId) {
        return mDatabase.messageDao().getUnReadMessages(conversationId);
    }

    public void updateMessagesReadStatus(List<MessageEntity> list){
        mDatabase.messageDao().updateMessagesReadStatus(list);
    }

    public void updateMessageSendStatus(MessageEntity me){
        mDatabase.messageDao().updateMessageSendStatus(me);
    }

//    public DataSource.Factory<Integer, MessageEntity> getMessagesForPaging(final long conversationId){
//        return mDatabase.messageDao().getPageMessages(conversationId);
//    }

    public LiveData<MessageEntity> getMessage(final long messageId){
        return mDatabase.messageDao().getMessage(messageId);
    }

    public long getConversationId(final String contactNumber){
        return mDatabase.conversationDao().getConversationId(contactNumber);
    }

    public long insertConversation(ConversationEntity ce){
        return mDatabase.conversationDao().insertConversation(ce);
    }

    public void updateConversation(ConversationEntity ce){
        mDatabase.conversationDao().updateConversation(ce);
    }

    public void deleteConversation(long conversationId){
        Log.i("Junwang", "deleteConversation conversationId="+conversationId);
        mDatabase.conversationDao().deleteConversation(conversationId);
    }

    public void deleteConversation(ConversationEntity ce){
        Log.i("Junwang", "deleteConversation ConversationEntity");
        mDatabase.conversationDao().deleteConversation(ce);
    }

    public long insertMessage(MessageEntity messageEntity){
        return mDatabase.messageDao().insertMessage(messageEntity);
    }

    public void insertUserInfo(UserInfoEntity userInfoEntity){
        mDatabase.userInfoDao().insertUserInfo(userInfoEntity);
    }

    public LiveData<List<UserInfoEntity>> getUsers(){
        return mDatabase.userInfoDao().getUsers();
    }

    public LiveData<UserInfoEntity> getUser(String uri){
        return mDatabase.userInfoDao().getUser(uri);
    }

    public LiveData<List<UserInfoEntity>> getLatestUsedChatbotList(){
        return mDatabase.userInfoDao().getLatestUsedChatbotList();
    }

    public LiveData<List<UserInfoEntity>> getAttentionedChatbotList(){
        return mDatabase.userInfoDao().getAttentionedChatbotList();
    }

    public int getUnreadCount(long conversationId){
        return mDatabase.messageDao().getUnreadCount(conversationId);
    }

    public void updateMessageReadStatus(long conversationId){
//        mDatabase.conversationDao().updateMessageReadStatus(conversationId);
    }

    public LiveData<MessageEntity> getLastMessage(long conversationId){
        return mDatabase.messageDao().getLastMessage(conversationId);
    }

    public LiveData<ConversationEntity> getConversationByChatbotId(String chatbotId){
        return mDatabase.conversationDao().getConversationByChatbotId(chatbotId);
    }

    public void deleteMessage(long messageId){
        mDatabase.messageDao().deleteMessage(messageId);
    }

    public void deleteMessage(MessageEntity me){
        mDatabase.messageDao().deleteMessage(me);
    }

    public void deleteMessages(List<MessageEntity> messageEntityList){
        mDatabase.messageDao().deleteMessages(messageEntityList);
    }

    public LiveData<UserInfoEntity> getUserInfoByConversationId(final long conversationId){
        return mDatabase.userInfoDao().getUserInfoByConversationId(conversationId);
    }

    public LiveData<List<MessageUserInfoEntity>> getAllMessages(){
        return mDatabase.messageDao().getAllMessages();
    }

    public LiveData<List<MessageUserInfoEntity>> getFavoritedMessages(){
        return mDatabase.messageDao().getFavoritedMessages();
    }

    public void updateUserInfo(UserInfoEntity userInfoEntity){
        mDatabase.userInfoDao().updateUserInfo(userInfoEntity);
    }

    public void updateAttentionByChatbotId(String chatbotId, int isAttentioned){
        mDatabase.userInfoDao().updateAttentionByChatbotId(chatbotId, isAttentioned);
    }

    public void updateMessageFavoriteStatusById(long messageId, int isFavorited, long favoritedTimestamp){
        mDatabase.messageDao().updateMessageFavoriteStatusById(messageId, isFavorited, favoritedTimestamp);
    }
}
