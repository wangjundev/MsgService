package com.stv.msgservice.datamodel.viewmodel;

import android.app.Application;
import android.net.Uri;

import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.ui.conversation.message.ImageMessageContent;
import com.stv.msgservice.ui.conversation.message.TextMessageContent;

import java.io.File;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MessageViewModel extends AndroidViewModel {
    private final long mConversationId;
    private MutableLiveData<MessageEntity> messageLiveData;
    private MutableLiveData<MessageEntity> messageUpdateLiveData;
    private MutableLiveData<Message> messageRemovedLiveData;
    private MutableLiveData<Map<String, String>> mediaUploadedLiveData;
    private MutableLiveData<Object> clearMessageLiveData;
    private MutableLiveData<Map<String, Long>> messageDeliverLiveData;
//    private MutableLiveData<List<ReadEntry>> messageReadLiveData;

    private LiveData<List<MessageEntity>> mObservableMessages;
//    private LiveData<PagedList<MessageEntity>> mObservablePageMessages;
    private final DataRepository mRepository;

    public MessageViewModel(@NonNull Application application, DataRepository repository,
                            final long conversationId) {
        super(application);
        mConversationId = conversationId;
        mRepository = DataRepository.getInstance(AppDatabase.getInstance(application.getBaseContext()));
        mObservableMessages = repository.getMessages(mConversationId);
    }

    public long insertMessage(MessageEntity me){
        return mRepository.insertMessage(me);
    }

    /**
     * Expose the LiveData Comments query so the UI can observe it.
     */
    public LiveData<List<MessageEntity>> getMessages(final long conversationId) {
        mObservableMessages = mRepository.getMessages(conversationId);
        return mObservableMessages;
    }

    public LiveData<List<MessageEntity>> getMessages() {
        mObservableMessages = mRepository.getMessages(mConversationId);
        return mObservableMessages;
    }

    public void deleteMessage(long messageId){
        mRepository.deleteMessage(messageId);
    }

    public void deleteMessage(MessageEntity me){mRepository.deleteMessage(me);}

    //need to implement
    public void resendMessage(Message message){

    }

    public void sendTextMsg(Conversation conversation, TextMessageContent txtContent) {
//        sendMessage(conversation, txtContent);
    }

    public void sendTextMsg(long conversationId, String text){

    }

    public void saveDraft(Conversation conversation, String draftString) {
    }

    public void sendVideoMsg(Conversation conversation, File file) {
    }

    public void sendFileMsg(Conversation conversation, File file) {
    }

    public void sendAudioFile(Conversation conversation, Uri imageFileThumbUri, int duration) {
    }

    public void clearUnreadStatus(long conversationId){

    }

    public LiveData<List<MessageEntity>> loadOldMessages(long conversationId, long fromMessageId, int count){
        return mRepository.loadOldMessages(conversationId, fromMessageId, count);
    }

    public LiveData<List<MessageEntity>> loadNewMessages(long conversationId, long fromMessageId, int count){
        return mRepository.loadNewMessages(conversationId, fromMessageId, count);
    }

    public void sendImgMsg(Conversation conversation, Uri imageFileThumbUri, Uri imageFileSourceUri) {
        ImageMessageContent imgContent = new ImageMessageContent(imageFileSourceUri.getEncodedPath());
    }

    public void sendImgMsg(Conversation conversation, File imageFileThumb, File imageFileSource) {
        // Uri.fromFile()遇到中文檔名會轉 ASCII，這個 ASCII 的 path 將導致後面 ChatManager.sendMessage()
        // 在 new File()時找不到 File 而 return
        Uri imageFileThumbUri = Uri.parse(Uri.decode(imageFileThumb.getAbsolutePath()));
//        Uri imageFileThumbUri = Uri.fromFile(imageFileThumb);
        Uri imageFileSourceUri = Uri.parse(Uri.decode(imageFileSource.getAbsolutePath()));
//        Uri imageFileSourceUri = Uri.fromFile(imageFileSource);
        sendImgMsg(conversation, imageFileThumbUri, imageFileSourceUri);

    }

    public void sendStickerMsg(Conversation conversation, String localPath, String remoteUrl) {
    }

    public void onReceiveMessage(List<MessageEntity> messages, boolean hasMore) {
        if (messageLiveData != null && messages != null) {
            for (MessageEntity msg : messages) {
                messageLiveData.postValue(msg);
            }
        }
    }

    public void onDeleteMessage(Message message) {
        if (messageRemovedLiveData != null) {
            messageRemovedLiveData.postValue(message);
        }
    }

    public void onSendSuccess(MessageEntity message){
        if (messageUpdateLiveData != null) {
            messageUpdateLiveData.postValue(message);
        }
    }

    void onSendFail(MessageEntity message, int errorCode){
        if (messageUpdateLiveData != null) {
            messageUpdateLiveData.postValue(message);
        }
    }

    public MutableLiveData<MessageEntity> messageLiveData() {
        if (messageLiveData == null) {
            messageLiveData = new MutableLiveData<>();
        }
        return messageLiveData;
    }

    public MutableLiveData<MessageEntity> messageUpdateLiveData() {
        if (messageUpdateLiveData == null) {
            messageUpdateLiveData = new MutableLiveData<>();
        }
        return messageUpdateLiveData;
    }

    public MutableLiveData<Message> messageRemovedLiveData() {
        if (messageRemovedLiveData == null) {
            messageRemovedLiveData = new MutableLiveData<>();
        }
        return messageRemovedLiveData;
    }

    public MutableLiveData<Map<String, String>> mediaUpdateLiveData() {
        if (mediaUploadedLiveData == null) {
            mediaUploadedLiveData = new MutableLiveData<>();
        }
        return mediaUploadedLiveData;
    }

    public MutableLiveData<Object> clearMessageLiveData() {
        if (clearMessageLiveData == null) {
            clearMessageLiveData = new MutableLiveData<>();
        }
        return clearMessageLiveData;
    }

    public MutableLiveData<Map<String, Long>> messageDeliverLiveData() {
        if (messageDeliverLiveData == null) {
            messageDeliverLiveData = new MutableLiveData<>();
        }
        return messageDeliverLiveData;
    }

//    public LiveData<PagedList<MessageEntity>> getPageMessages(){
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setPageSize(10)    //每页显示的词条数
//                .setEnablePlaceholders(false)
//                .setInitialLoadSizeHint(10) //首次加载的数据量
//                .setPrefetchDistance(5)     //距离底部还有多少条数据时开始预加载
//                .build();
//        mObservablePageMessages = new LivePagedListBuilder<Integer,MessageEntity>(mRepository.getMessagesForPaging(mConversationId), config).build();
//        return mObservablePageMessages;
//    }

    /**
     * A creator is used to inject the product ID into the ViewModel
     * <p>
     * This creator is to showcase how to inject dependencies into ViewModels. It's not
     * actually necessary in this case, as the product ID can be passed in a public method.
     */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final long mConversationId;

        private final DataRepository mRepository;

        public Factory(@NonNull Application application, long conversationId) {
            mApplication = application;
            mConversationId = conversationId;
            mRepository = DataRepository.getInstance(AppDatabase.getInstance(application.getApplicationContext()));
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MessageViewModel(mApplication, mRepository, mConversationId);
        }
    }
}
