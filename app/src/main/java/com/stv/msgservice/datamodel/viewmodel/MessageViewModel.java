package com.stv.msgservice.datamodel.viewmodel;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.datamodel.TerminalInfo.DeviceIdUtil;
import com.stv.msgservice.datamodel.TerminalInfo.TerminalInfo;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.model.Message;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.network.DataEncryptInterceptor;
import com.stv.msgservice.datamodel.network.DeliveryInfo;
import com.stv.msgservice.datamodel.network.InboundMessage;
import com.stv.msgservice.datamodel.network.SendCallback;
import com.stv.msgservice.datamodel.network.ServiceCapability;
import com.stv.msgservice.datamodel.network.UploadFileCallback;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotFileData;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotFileInfo;
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.ui.conversation.message.ImageMessageContent;
import com.stv.msgservice.ui.conversation.message.TextMessageContent;
import com.stv.msgservice.utils.FileUtils;
import com.stv.msgservice.xmlprocess.XmlUtils;
import com.thoughtworks.xstream.XStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
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

    public LiveData<MessageEntity> getMessage(long messageId){
        return mRepository.getMessage(messageId);
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

    public void deleteMessage(MessageEntity me, MutableLiveData<Message> msgRemovedLiveData){
        mRepository.deleteMessage(me);
//        if (msgRemovedLiveData != null) {
//            Log.i("Junwang", "update delete Message");
//            msgRemovedLiveData.setValue(me);
//        }
    }

    public void deleteMessages(List<MessageEntity> messageEntityList){
        mRepository.deleteMessages(messageEntityList);
    }

    public void deleteMessages(long conversationId){

    }

    //need to implement
//    public void resendMessage(MessageEntity message){
//        deleteMessage(message);
//    }

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

    public void sendReadReport(MessageEntity me, String senderAddress, String destinationAddress){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);

//            DeliveryInfo deliveryInfo = new DeliveryInfo(destinationAddress, me.getMessageId(), MessageConstants.MESSAGEDISPLAYED);
//            DeliveryInfoNotification deliveryInfoNotification = new DeliveryInfoNotification(deliveryInfo);
//            XStream xStream = new XStream();
//            xStream.aliasType("msg:deliveryInfoNotification", DeliveryInfoNotification.class);
//            String requestXml = xStream.toXML(deliveryInfoNotification);
//            Log.i("Junwang", "requestXml="+requestXml);
//            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

            String xmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<msg:deliveryInfoNotification xmlns:msg=\"urn:oma:xml:rest:netapi:messaging:1\">\n";
            String xmlSuffix = "\n</msg:deliveryInfoNotification>";
            DeliveryInfo deliveryInfo = new DeliveryInfo(destinationAddress, me.getMessageId(), MessageConstants.MESSAGEDISPLAYED);
//        DeliveryInfoNotification deliveryInfoNotification = new DeliveryInfoNotification(deliveryInfo);
            XStream xStream = new XStream();
            xStream.aliasType("deliveryInfo", DeliveryInfo.class);
            String requestXml = xmlPrefix+xStream.toXML(deliveryInfo)+xmlSuffix;
            Log.i("Junwang", "requestXml="+requestXml);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

            String concatUrl = MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), senderAddress, null);
            Log.i("Junwang", "concatUrl="+concatUrl);

            Flowable<retrofit2.Response<Void>> response = service.sendStatusReport(body, "/5gcallback/api/catherine/DeliveryInfoNotification/"+concatUrl);
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
//            Observable<BaseResult<MessageEntity>> response = service.createCommit(body, null);
//            try{
//                response
////                        .subscribeOn(Schedulers.io())
////                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
//                        .subscribe(new Consumer<BaseResult<MessageEntity>>() {
//                            @Override
//                            public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
//                                if(messageEntityBaseResult != null){
//                                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
//                                    messageUpdateLiveData.setValue(msg);
//                                }
//                            }
//                        }, new Consumer<Throwable>() {
//                            @Override
//                            public void accept(Throwable throwable) throws Exception {
//                                Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
//                                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                                if(messageUpdateLiveData != null){
//                                    Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
//                                    messageUpdateLiveData.setValue(msg);
//                                }
//                                throwable.printStackTrace();
//                            }
//                        });
//            }catch (Exception e){
//                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
//                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                if(messageUpdateLiveData != null){
//                    messageUpdateLiveData.setValue(msg);
//                }
//            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if(messageUpdateLiveData != null){
                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                messageUpdateLiveData.setValue(msg);
            }
        }
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
            String androidId = TerminalInfo.getAndroidId(context);
            String serial = TerminalInfo.getSERIAL();
            Log.i("Junwang", "androidId="+androidId+",serial="+serial);
            String deviceId = DeviceIdUtil.getDeviceId(context);
            Log.i("Junwang", "deviceId="+deviceId);
            sHttpsReqUriBuilder.appendQueryParameter(PARAM_CLIENT_SERIAL, deviceId);
        }
        final Uri.Builder uriBuilder = sHttpsReqUriBuilder.build().buildUpon();
        Log.i("Junwang", "url params="+uriBuilder.toString());
        return uriBuilder.toString();
    }

    private String generateFileBodyText(MessageEntity msg, String filePath, String fileUrl){
        try{
            File file = new File(filePath);
            long fileSize = file.length();
            String fileName = file.getName();
            String fileType = msg.getAttachmentType();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh-MM-ss");
            String nowStr=sdf.format(new Date());
            long untilTime = sdf.parse(nowStr).getTime()+30*24*60*60*1000;
            String time = sdf.format(new Date(untilTime));
            Log.i("Junwang", "send untilTime="+untilTime);
//            ChatbotFileData chatbotFileData = new ChatbotFileData(/*"http://172.16.0.95:80/Users/wjmbp/Downloads/chfsmac/"+fileName*/fileUrl, time);
//            ChatbotFileInfo fileInfo = new ChatbotFileInfo("file", fileSize, fileName, fileType, chatbotFileData);
            ChatbotFileData chatbotFileData = new ChatbotFileData();
            chatbotFileData.setUrl(fileUrl);
            chatbotFileData.setUntil(untilTime+"");
            ChatbotFileInfo fileInfo = new ChatbotFileInfo();
            fileInfo.setType("file");
            fileInfo.setFileSize(fileSize);
            fileInfo.setFileName(fileName);
            fileInfo.setContentType(fileType);
            fileInfo.setData(chatbotFileData);

            ChatbotFileInfo thumbnailInfo = null;
            if(fileType != null && fileType.startsWith("image")){
//                thumbnailInfo = new ChatbotFileInfo("thumbnail", fileSize, fileName, fileType, chatbotFileData);
                thumbnailInfo = new ChatbotFileInfo();
                thumbnailInfo.setType("thumbnail");
                thumbnailInfo.setFileSize(fileSize);
                thumbnailInfo.setFileName(fileName);
                thumbnailInfo.setContentType(fileType);
                thumbnailInfo.setData(chatbotFileData);
            }
            String fileBodyText = getFileBodyText(fileInfo, thumbnailInfo);
            return fileBodyText;
        }catch (Exception e){
            Log.i("Junwang", "generateFileBodyText "+e.toString());
        }
        return null;
    }

    public void sendFilemsg(MessageEntity msg, String from, String to, String conversationId, String filePath, SendCallback callback, MutableLiveData<MessageEntity> msgLiveData, MutableLiveData<MessageEntity> msgUpdateLiveData){
        String concatUrl = MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), to, null);
        Log.i("Junwang", "send filemsg concatUrl="+concatUrl);
        try{
            FileUtils.upload(new File(filePath), /*"http://172.16.0.95:80/Users/wjmbp/Downloads/chfsmac"*/MessageConstants.BASE_URL+"5gcallback/api/catherine/upload/"+concatUrl, new UploadFileCallback() {
                @Override
                public void onSuccess(String url) {
                    Log.i("Junwang", "upload file to server success");
                    String fileInserverUrl = null;
                    if(url == null){
                        File file = new File(filePath);
                        fileInserverUrl = MessageConstants.BASE_URL+"5gcallback/api/catherine/upload/"+file.getName();
                    }else{
                        fileInserverUrl = url;
                    }
                    try{
                        String fileBodyText = generateFileBodyText(msg, filePath, fileInserverUrl);

                        ServiceCapability serviceCapability = new ServiceCapability("ChatbotSA", "+g.gsma.rcs.botversion=\\\"#=1\\\"");
                        String sendconversationId = null;
                        if(conversationId == null){
                            sendconversationId = UUID.randomUUID().toString();
                        }else{
                            sendconversationId = conversationId;
                        }
                        String contributionId = UUID.randomUUID().toString();
                        InboundMessage inboundMessage = new InboundMessage(to, from,
                                null, contributionId, fileBodyText, "text/plain", "encoding=UTF-8", serviceCapability,
                                sendconversationId, contributionId);
                        XStream xStream = new XStream();
                        xStream.alias("inboundMessage", InboundMessage.class);
                        String sendXmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                                "<msg:inboundMessageNotification xmlns:msg=\"urn:oma:xml:rest:netapi:messaging:1\">\n";
                        String sendXmlSuffix = "\n</msg:inboundMessageNotification>";
                        String requestXml = sendXmlPrefix+ XmlUtils.toXML(inboundMessage)+sendXmlSuffix;
                        Log.i("Junwang", "sendFilemsg requestXml="+requestXml);
                        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(MessageConstants.BASE_URL)
                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ApiService service = retrofit.create(ApiService.class);
                        Observable<retrofit2.Response<Void>> response = service.createCommit(body, "/5gcallback/api/catherine/InboundMessageNotification/"+concatUrl);
                        try{
                            response
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                                .subscribe(new Consumer<retrofit2.Response>() {
                                    @Override
                                    public void accept(retrofit2.Response responseBody) throws Exception {
                                        Log.i("Junwang",  "sendTextmsg accepted"+"," + Thread.currentThread().getName());
                                        int retCode = responseBody.code();
                                        Log.i("Junwang", "return code = "+retCode);
                                        if(retCode == 204 || retCode == 200) {
                                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                                        }
                                        if(msgUpdateLiveData != null){
                                            msgUpdateLiveData.setValue(msg);
                                        }
                                        if(callback != null){
                                            callback.onSuccess();
                                        }
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e("Junwang",  "accept throwable "+throwable.toString()+"," + Thread.currentThread().getName());
                                        msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                                        if(msgUpdateLiveData != null){
                                            Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                                            msgUpdateLiveData.setValue(msg);
                                        }
                                    }
                                });
                        }catch (Exception e){
                            Log.e("Junwang",  "sendTextmsg exception "+e.toString());
                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                            if(msgUpdateLiveData != null){
                                msgUpdateLiveData.setValue(msg);
                            }
                        }
                    }catch (Exception e){
                        Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
                        msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                        if(msgUpdateLiveData != null){
                            Log.e("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                            msgUpdateLiveData.setValue(msg);
                        }
                    }
                }

                @Override
                public void onFail(int errorCode) {
                    Log.e("Junwang", "upload file failed errorCode="+errorCode);
//                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                    if(msgUpdateLiveData != null){
                        msgUpdateLiveData.setValue(msg);
                    }
                }
            });
        }catch (Exception e){
            Log.e("Junwang", "uploadChatbotFile "+e.toString());
        }
    }

    private String getFileInfoXMLString(ChatbotFileInfo fileInfo){
        if(fileInfo == null){
            return null;
        }
        String fileInfoString = null;
        try{
            Serializer se=new Persister();
            File file = new File(getApplication().getFilesDir()+"xmlSerial");
            Writer writer = new FileWriter(file);
            se.write(fileInfo, writer);
            FileReader fr=null;
            try
            {
                fr=new FileReader(file);
                char[] buf=new char[1024];
                int num=0;
                while((num=fr.read(buf))!=-1)
                {
                    fileInfoString = new String(buf,0,num);
                    Log.i("Junwang", "fileInfoString="+fileInfoString);
                }
            }
            catch(IOException e)
            {
                Log.i("Junwang", "getFileInfoXMLString FileReader "+e.toString());
            }
            finally
            {
                try
                {
                    if(fr!=null)
                    {
                        fr.close();
                    }
                    if(file != null){
                        file.delete();
                    }
                }
                catch(IOException e)
                {
                    Log.i("Junwang", "getFileInfoXMLString FileReader "+e.toString());
                }
            }
        }catch(Exception e){
            Log.i("Junwang", "getFileInfoXMLString fr.close "+e.toString());
        }
        return fileInfoString;
    }

    private String getFileBodyText(ChatbotFileInfo fileInfo, ChatbotFileInfo thumbnailInfo){
        String xmlPrefix = "<![CDATA[\n" +
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<file xmlns=\"urn:gsma:params:xml:ns:rcs:rcs:fthttp\">\n";
        String xmlSuffix = "</file>]]>";
        String fileInfoString = getFileInfoXMLString(fileInfo);
        String thumbnailInfoString = getFileInfoXMLString(thumbnailInfo);
        StringBuffer fileBodyXml = new StringBuffer(xmlPrefix);
        if(thumbnailInfoString != null){
            fileBodyXml.append(thumbnailInfoString+"\n");
        }
        if(fileInfoString != null){
            fileBodyXml.append(fileInfoString+"\n");
        }
        fileBodyXml.append(xmlSuffix);
        String fileString = fileBodyXml.toString();
        Log.i("Junwang", "fileBodyXml="+fileString);
        return fileString;
    }

    private String getRequestXml(String conversationId, String from, String to, String text, String xmlPrefix, String xmlSuffix){
        ServiceCapability serviceCapability = new ServiceCapability("ChatbotSA", "+g.gsma.rcs.botversion=\\\"#=1\\\"");
        String sendconversationId = null;
        if(conversationId == null){
            sendconversationId = UUID.randomUUID().toString();
        }else{
            sendconversationId = conversationId;
        }
        String contributionId = UUID.randomUUID().toString();
        InboundMessage inboundMessage = new InboundMessage(to, from,
                null, contributionId, text, "text/plain", "encoding=UTF-8", serviceCapability,
                sendconversationId, contributionId);
        XStream xStream = new XStream();
//            xStream.aliasType("msg:inboundMessage", InboundMessage.class);
        xStream.alias("inboundMessage", InboundMessage.class);
//        String sendXmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
//                "<msg:inboundMessageNotification xmlns:msg=\"urn:oma:xml:rest:netapi:messaging:1\">\n";
//        String sendXmlSuffix = "\n</msg:inboundMessageNotification>";
        String requestXml = xmlPrefix+xStream.toXML(inboundMessage)+xmlSuffix;
        Log.i("Junwang", "requestXml="+requestXml);
        return requestXml;
    }

    public void sendTextmsg(Context context, String from, String to, String conversationId, MessageEntity msg, String text, SendCallback callback, MutableLiveData<MessageEntity> msgLiveData, MutableLiveData<MessageEntity> msgUpdateLiveData){
        try{
//            OkHttpClient.Builder client = new OkHttpClient.Builder();
//            client.addInterceptor(new Interceptor() {
//                @Override
//                public Response intercept(Interceptor.Chain chain) throws IOException {
//                    Request original = chain.request();
//                    Request request = original.newBuilder()
//                            .header("User-Agent", getHttpsRequestArguments(getApplication().getApplicationContext(), to, null))
//                            .header("Accept", "application/xml")
//                            .method(original.method(), original.body())
//                            .build();
//
//                    return chain.proceed(request);
//                }
//            });
//
//            OkHttpClient httpClient = client.build();

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
            Log.i("Junwang", "send destination="+to);
            ServiceCapability serviceCapability = new ServiceCapability("ChatbotSA", "+g.gsma.rcs.botversion=\\\"#=1\\\"");
            String sendconversationId = null;
            if(conversationId == null){
                sendconversationId = UUID.randomUUID().toString();
            }else{
                sendconversationId = conversationId;
            }
            String contributionId = UUID.randomUUID().toString();
            InboundMessage inboundMessage = new InboundMessage(to, from,
                    null, contributionId, text, "text/plain;charset=UTF-8", "encoding=UTF-8", serviceCapability,
                    sendconversationId, contributionId);
            XStream xStream = new XStream();
//            xStream.aliasType("msg:inboundMessage", InboundMessage.class);
            xStream.alias("inboundMessage", InboundMessage.class);
            String sendXmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<msg:inboundMessageNotification xmlns:msg=\"urn:oma:xml:rest:netapi:messaging:1\">\n";
            String sendXmlSuffix = "\n</msg:inboundMessageNotification>";
            String requestXml = sendXmlPrefix+xStream.toXML(inboundMessage)+sendXmlSuffix;
            Log.i("Junwang", "requestXml="+requestXml);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

//            String postBody = ConversationListViewModel.getPostBodyJson(context, null, null, from);
//            Log.i("Junwang", "post body json is "+postBody);
//            RequestBody body1 = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);

            String concatUrl = MessageViewModel.getHttpsRequestArguments(getApplication().getApplicationContext(), to, null);
            Log.i("Junwang", "send textmsg concatUrl="+concatUrl);

            Observable<retrofit2.Response<Void>> response = service.createCommit(body, "/5gcallback/api/catherine/InboundMessageNotification/"+concatUrl);
            try{
                response
//                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                        .subscribe(new Consumer<retrofit2.Response>() {
                            @Override
                            public void accept(retrofit2.Response responseBody) throws Exception {
                                Log.i("Junwang",  "sendTextmsg accepted"+"," + Thread.currentThread().getName());
                                int retCode = responseBody.code();
                                Log.i("Junwang", "return code = "+retCode);
                                if(retCode == 204 || retCode == 200) {
                                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
                                }else{
                                    msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                                }
                                if(msgUpdateLiveData != null){
                                    msgUpdateLiveData.setValue(msg);
                                }
                                if(callback != null){
                                    callback.onSuccess();
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e("Junwang",  "accept throwable "+throwable.toString()+"," + Thread.currentThread().getName());
                                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                                if(msgUpdateLiveData != null){
                                    Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                                    msgUpdateLiveData.setValue(msg);
                                }
                            }
                        });
            }catch (Exception e){
                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
                if(msgUpdateLiveData != null){
                    msgUpdateLiveData.setValue(msg);
                }
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
            if(msgUpdateLiveData != null){
                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
                msgUpdateLiveData.setValue(msg);
            }
        }
    }

//    public void sendTextmsg(Context context, Conversation conversation, MessageEntity msg, String text, SendCallback callback){
//        try{
//            OkHttpClient.Builder client = new OkHttpClient.Builder();
//            client.addInterceptor(new Interceptor() {
//                  @Override
//                  public Response intercept(Interceptor.Chain chain) throws IOException {
//                      Request original = chain.request();
//                      Request request = original.newBuilder()
//                              .header("User-Agent", getHttpsRequestArguments(getApplication().getApplicationContext(), conversation.getSenderAddress(), null))
//                              .header("Accept", "application/xml")
//                              .method(original.method(), original.body())
//                              .build();
//
//                      return chain.proceed(request);
//                  }
//              });
//
//            OkHttpClient httpClient = client.build();
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl(MessageConstants.BASE_URL)
////                    .client(httpClient)
//                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//            ApiService service = retrofit.create(ApiService.class);
////            Gson gson=new Gson();
////            HashMap<String,String> paramsMap=new HashMap<>();
////            paramsMap.put("content",text);
////            String strEntity = gson.toJson(paramsMap);
////            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"),strEntity);
//            Log.i("Junwang", "send destination="+conversation.getSenderAddress());
//            ServiceCapability serviceCapability = new ServiceCapability("ChatbotSA", "+g.gsma.rcs.botversion=\\\"#=1\\\"");
//            String conversationId = conversation.getConversationID();
//            if(conversationId == null){
//                conversationId = UUID.randomUUID().toString();
//            }
//            String contributionId = UUID.randomUUID().toString();
//            InboundMessage inboundMessage = new InboundMessage(conversation.getSenderAddress(), conversation.getDestinationAddress(),
//                            null, contributionId, text, "text/plain", "encoding=UTF-8", serviceCapability,
//                    conversationId, contributionId);
//            XStream xStream = new XStream();
//            xStream.aliasType("msg:inboundMessage", InboundMessage.class);
////            xStream.alias("inboundMessage", null);
//            String requestXml = xStream.toXML(inboundMessage);
//            Log.i("Junwang", "requestXml="+requestXml);
//            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);
//
//            String postBody = ConversationListViewModel.getPostBodyJson(context, null, null, conversation.getSenderAddress());
//            Log.i("Junwang", "post body json is "+postBody);
//            RequestBody body1 = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);
//
//            Observable<BaseResult<MessageEntity>> response = service.createCommit(body, getHttpsRequestArguments(getApplication().getApplicationContext(), conversation.getSenderAddress(), null));
//            try{
//                response
////                        .subscribeOn(Schedulers.io())
////                        .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
//                        .subscribe(new Consumer<BaseResult<MessageEntity>>() {
//                    @Override
//                    public void accept(BaseResult<MessageEntity> messageEntityBaseResult) throws Exception {
//                        if(messageEntityBaseResult != null){
//                            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_COMPLETE);
//                            messageUpdateLiveData.setValue(msg);
//                            if(callback != null){
//                                callback.onSuccess();
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Log.e("Junwang",  "accept exception "+throwable.toString()+"," + Thread.currentThread().getName());
//                        msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                        if(messageUpdateLiveData != null){
//                            Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
//                            messageUpdateLiveData.setValue(msg);
//                        }
//                        throwable.printStackTrace();
//                    }
//                });
//            }catch (Exception e){
//                Log.e("Junwang",  "sendTextmsg exception "+e.toString());
//                msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//                if(messageUpdateLiveData != null){
//                    messageUpdateLiveData.setValue(msg);
//                }
//            }
//        }catch (Exception e){
//            Log.e("Junwang",  "retrofit sendTextmsg exception "+e.toString()+"," + Thread.currentThread().getName());
//            msg.setMessageStatus(MessageConstants.BUGLE_STATUS_OUTGOING_FAILED);
//            if(messageUpdateLiveData != null){
//                Log.i("Junwang",  "update msgid="+msg.getId()+" status to send fail.");
//                messageUpdateLiveData.setValue(msg);
//            }
//        }
//    }

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
