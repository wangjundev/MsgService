package com.stv.msgservice.datamodel.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.datamodel.TerminalInfo.TerminalInfo;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.network.BaseResult;
import com.stv.msgservice.datamodel.network.DataEncryptInterceptor;
import com.stv.msgservice.datamodel.network.DeliveryInfo;
import com.stv.msgservice.datamodel.network.DeliveryInfoNotification;
import com.stv.msgservice.datamodel.network.InboundMessage;
import com.stv.msgservice.datamodel.network.SendCallback;
import com.stv.msgservice.datamodel.network.ServiceCapability;
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.ui.conversation.message.ImageMessageContent;
import com.stv.msgservice.ui.conversation.message.TextMessageContent;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageViewModel extends AndroidViewModel {
    private final long mConversationId;
    private MutableLiveData<MessageEntity> messageLiveData;
    private MutableLiveData<MessageEntity> messageUpdateLiveData;
    private MutableLiveData<Message> messageRemovedLiveData;
    private MutableLiveData<Map<String, String>> mediaUploadedLiveData;
    private MutableLiveData<Object> clearMessageLiveData;
    private MutableLiveData<Map<String, Long>> messageDeliverLiveData;
    private LiveData<List<MessageEntity>>  messageUnReadLiveData;
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

//    public LiveData<List<MessageEntity>> loadAroundMessages(final long conversationId){
//
//    }

    public LiveData<List<MessageEntity>> getUnReadMessages(final long conversationId) {
        messageUnReadLiveData = mRepository.getUnReadMessages(conversationId);
        return messageUnReadLiveData;
    }

    public void updateMessagesReadStatus(List<MessageEntity> list){
        mRepository.updateMessagesReadStatus(list);
    }

    public void updateMessageSendStatus(MessageEntity me){
        mRepository.updateMessageSendStatus(me);
    }

    public LiveData<List<MessageEntity>> getMessages() {
        mObservableMessages = mRepository.getMessages(mConversationId);
        return mObservableMessages;
    }

    public List<MessageEntity> searchMessages(long conversationId, String query) {
        return mRepository.searchMessages(conversationId, query);
    }

//    public void deleteMessage(long messageId){
//        mRepository.deleteMessage(messageId);
//    }

    public void deleteMessage(MessageEntity me){
        if (messageRemovedLiveData != null) {
            Log.i("Junwang", "update delete Message");
            messageRemovedLiveData.setValue(me);
        }
        mRepository.deleteMessage(me);
    }

    public void deleteMessages(List<MessageEntity> messageEntityList){
        mRepository.deleteMessages(messageEntityList);
    }

    public void deleteMessages(long conversationId){

    }

    //need to implement
    public void resendMessage(MessageEntity message){
        deleteMessage(message);
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
        Log.i("Junwang", "get messageUpdateLiveData");
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

    public void sendReadReport(MessageEntity me, String address){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);

            DeliveryInfo deliveryInfo = new DeliveryInfo(address, me.getMessageId(), MessageConstants.MESSAGEDISPLAYED);
            DeliveryInfoNotification deliveryInfoNotification = new DeliveryInfoNotification(deliveryInfo);
            XStream xStream = new XStream();
            xStream.aliasType("msg:deliveryInfoNotification", DeliveryInfoNotification.class);
//            xStream.alias("inboundMessage", null);
            String requestXml = xStream.toXML(deliveryInfoNotification);
            Log.i("Junwang", "requestXml="+requestXml);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

            Flowable<retrofit2.Response<Void>> response = service.sendStatusReport(body, address);
            try{
                response.subscribe(new Consumer<retrofit2.Response>() {
                    @Override
                    public void accept(retrofit2.Response responseBody) throws Exception {
                        Log.e("Junwang",  "sendReadReport successful.");
                        me.setDeliveryStatus(MessageConstants.MESSAGEREADREPORTFAILED);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Junwang",  "sendReadReport throwable "+throwable.toString());
                        me.setDeliveryStatus(MessageConstants.MESSAGEREADREPORTFAILED);
                    }
                });
            }catch (Exception e){
                Log.e("Junwang",  "sendReadReport exception "+e.toString());
                me.setDeliveryStatus(MessageConstants.MESSAGEREADREPORTFAILED);
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendReadReport exception "+e.toString()+"," + Thread.currentThread().getName());
            me.setDeliveryStatus(MessageConstants.MESSAGEREADREPORTFAILED);
        }
        mRepository.updateMessageSendStatus(me);
    }


    public void sendLocationMessage(MessageEntity msg, LocationData locationData){
        try{
            OkHttpClient client = new OkHttpClient.Builder().addNetworkInterceptor(new DataEncryptInterceptor()).build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(MessageConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);
            Gson gson=new Gson();

            HashMap<String,String> paramsMap=new HashMap<>();
            paramsMap.put("locationData",gson.toJson(locationData));
            paramsMap.put("phonenumber", "");
            paramsMap.put("filetype", "");
            paramsMap.put("fileurl", "");
            paramsMap.put("msgid", "");
            paramsMap.put("chatbotid", "");

            String strEntity = gson.toJson(paramsMap);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
            Observable<BaseResult<MessageEntity>> response = service.createCommit(body, null);
            try{
                response
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                        .subscribe(new Consumer<BaseResult<MessageEntity>>() {
                            @Override
                            public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
                                if(messageEntityBaseResult != null){
                                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                                    messageUpdateLiveData.setValue(msg);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
                                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                                if(messageUpdateLiveData != null){
                                    Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                                    messageUpdateLiveData.setValue(msg);
                                }
                                throwable.printStackTrace();
                            }
                        });
            }catch (Exception e){
                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                if(messageUpdateLiveData != null){
                    messageUpdateLiveData.setValue(msg);
                }
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if(messageUpdateLiveData != null){
                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                messageUpdateLiveData.setValue(msg);
            }
        }
    }

    public void sendFilemsg(MessageEntity msg, String filePath, SendCallback callback){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);
            Gson gson=new Gson();
            HashMap<String,String> paramsMap=new HashMap<>();
            paramsMap.put("filepath",filePath);
            String strEntity = gson.toJson(paramsMap);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),strEntity);
            Observable<BaseResult<MessageEntity>> response = service.createCommit(body, null);
            try{
                response
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                        .subscribe(new Consumer<BaseResult<MessageEntity>>() {
                            @Override
                            public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
                                if(messageEntityBaseResult != null){
                                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                                    messageUpdateLiveData.setValue(msg);
                                    if(callback != null){
                                        callback.onSuccess();
                                    }
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
                                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                                if(messageUpdateLiveData != null){
                                    Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                                    messageUpdateLiveData.setValue(msg);
                                }
                                throwable.printStackTrace();
                            }
                        });
            }catch (Exception e){
                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                if(messageUpdateLiveData != null){
                    messageUpdateLiveData.setValue(msg);
                }
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if(messageUpdateLiveData != null){
                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                messageUpdateLiveData.setValue(msg);
            }
        }

//        try{
//            FileUtils.uploadChatbotFile(new File(filePath), filePath, new UploadFileCallback() {
//                @Override
//                public void onSuccess(String url) {
//                    try{
//                        Retrofit retrofit = new Retrofit.Builder()
//                                .baseUrl(/*MessageConstants.BASE_URL*/url)
//                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//
//                        ApiService service = retrofit.create(ApiService.class);
//                        Gson gson=new Gson();
//                        HashMap<String,String> paramsMap=new HashMap<>();
//                        paramsMap.put("filepath",filePath);
//                        String strEntity = gson.toJson(paramsMap);
//                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
//                        Observable<BaseResult<MessageEntity>> response = service.createCommit(body, null);
//                        try{
//                            response
////                        .subscribeOn(Schedulers.io())
////                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
//                                    .subscribe(new Consumer<BaseResult<MessageEntity>>() {
//                                        @Override
//                                        public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
//                                            if(messageEntityBaseResult != null){
//                                                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
//                                                messageUpdateLiveData.setValue(msg);
//                                                if(callback != null){
//                                                    callback.onSuccess();
//                                                }
//                                            }
//                                        }
//                                    }, new Consumer<Throwable>() {
//                                        @Override
//                                        public void accept(Throwable throwable) throws Exception {
//                                            Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
//                                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                                            if(messageUpdateLiveData != null){
//                                                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
//                                                messageUpdateLiveData.setValue(msg);
//                                            }
//                                            throwable.printStackTrace();
//                                        }
//                                    });
//                        }catch (Exception e){
//                            Log.e("Junwang",  "sendTextmsg exception "+e.toString());
//                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                            if(messageUpdateLiveData != null){
//                                messageUpdateLiveData.setValue(msg);
//                            }
//                        }
//                    }catch (Exception e){
//                        Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
//                        msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                        if(messageUpdateLiveData != null){
//                            Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
//                            messageUpdateLiveData.setValue(msg);
//                        }
//                    }
//                }
//
//                @Override
//                public void onFail(int errorCode) {
//                    Log.i("Junwang", "upload file failed errorCode="+errorCode);
//                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                    if(messageUpdateLiveData != null){
//                        messageUpdateLiveData.setValue(msg);
//                    }
//                }
//            });
//        }catch (Exception e){
//            Log.i("Junwang", "upload file failed "+e.toString());
//            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//            if(messageUpdateLiveData != null){
//                messageUpdateLiveData.setValue(msg);
//            }
//        }
    }
    private static Uri.Builder sHttpsReqUriBuilder;
    private static final String PARAM_ID = "id";
    private static final String PARAM_CLIENT_VENDOR = "client_vendor";
    private static final String PARAM_TERMINAL_SW_VERSION = "client_version";
    private static final String PARAM_CLIENT_SERIAL = "client_serial";
    private static final String PARAM_SEARCH = "q";
    public static String getHttpsRequestArguments(Context context, String chatbotId, String keyword) {
        if (sHttpsReqUriBuilder == null) {
            sHttpsReqUriBuilder = new Uri.Builder();
            if(chatbotId != null) {
                sHttpsReqUriBuilder.appendQueryParameter(PARAM_ID, chatbotId);
            }
            if(keyword != null){
                sHttpsReqUriBuilder.appendQueryParameter(PARAM_SEARCH, keyword);
            }
            sHttpsReqUriBuilder.appendQueryParameter(PARAM_CLIENT_VENDOR,
                    TerminalInfo.getClientVendor());
            sHttpsReqUriBuilder.appendQueryParameter(PARAM_TERMINAL_SW_VERSION,
                    TerminalInfo.getTerminalSoftwareVersion());
            sHttpsReqUriBuilder.appendQueryParameter(PARAM_CLIENT_SERIAL, TerminalInfo.getMEID(context)+"_"+TerminalInfo.getVerName(context));
        }
        final Uri.Builder uriBuilder = sHttpsReqUriBuilder.build().buildUpon();
        Log.i("Junwang", "url params="+uriBuilder.toString());
        return uriBuilder.toString();
    }

    public void sendTextmsg(Conversation conversation, MessageEntity msg, String text, SendCallback callback){
        try{
            OkHttpClient.Builder client = new OkHttpClient.Builder();
            client.addInterceptor(new Interceptor() {
                  @Override
                  public Response intercept(Interceptor.Chain chain) throws IOException {
                      Request original = chain.request();
                      Request request = original.newBuilder()
                              .header("User-Agent", getHttpsRequestArguments(getApplication().getApplicationContext(), conversation.getSenderAddress(), null))
                              .header("Accept", "application/xml")
                              .method(original.method(), original.body())
                              .build();

                      return chain.proceed(request);
                  }
              });

            OkHttpClient httpClient = client.build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
//                    .client(httpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);
//            Gson gson=new Gson();
//            HashMap<String,String> paramsMap=new HashMap<>();
//            paramsMap.put("content",text);
//            String strEntity = gson.toJson(paramsMap);
//            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
            Log.i("Junwang", "send destination="+conversation.getSenderAddress());
            ServiceCapability serviceCapability = new ServiceCapability("ChatbotSA", "+g.gsma.rcs.botversion=\\\"#=1\\\"");
            String conversationId = conversation.getConversationID();
            if(conversationId == null){
                conversationId = UUID.randomUUID().toString();
            }
            String contributionId = UUID.randomUUID().toString();
            InboundMessage inboundMessage = new InboundMessage(conversation.getSenderAddress(), conversation.getDestinationAddress(),
                            null, contributionId, text, "text/plain", "encoding=UTF-8", serviceCapability,
                    conversationId, contributionId);
            XStream xStream = new XStream();
            xStream.aliasType("inboundMessage", InboundMessage.class);
//            xStream.alias("inboundMessage", null);
            String requestXml = xStream.toXML(inboundMessage);
            Log.i("Junwang", "requestXml="+requestXml);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

            Observable<BaseResult<MessageEntity>> response = service.createCommit(body, getHttpsRequestArguments(getApplication().getApplicationContext(), conversation.getSenderAddress(), null));
            try{
                response
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                        .subscribe(new Consumer<BaseResult<MessageEntity>>() {
                    @Override
                    public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
                        if(messageEntityBaseResult != null){
                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                            messageUpdateLiveData.setValue(msg);
                            if(callback != null){
                                callback.onSuccess();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
                        msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                        if(messageUpdateLiveData != null){
                            Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                            messageUpdateLiveData.setValue(msg);
                        }
                        throwable.printStackTrace();
                    }
                });
            }catch (Exception e){
                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                if(messageUpdateLiveData != null){
                    messageUpdateLiveData.setValue(msg);
                }
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if(messageUpdateLiveData != null){
                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                messageUpdateLiveData.setValue(msg);
            }
        }
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
