package com.stv.msgservice.datamodel.viewmodel;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.model.Conversation;
import com.stv.msgservice.datamodel.network.ApiService;
import com.stv.msgservice.datamodel.network.chatbot.CardContent;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMultiCard;
import com.stv.msgservice.datamodel.network.chatbot.MultiCardChatbotMsg;
import com.stv.msgservice.third.activity.LocationData;
import com.stv.msgservice.utils.FileUtils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import io.reactivex.functions.Consumer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import android.os.FileUtils;

public class ConversationListViewModel extends AndroidViewModel {
    private static final String QUERY_KEY = "QUERY";

    private final SavedStateHandle mSavedStateHandler;
    private final DataRepository mRepository;
    private final LiveData<List<ConversationEntity>> mConversations;

    public ConversationListViewModel(@NonNull Application application,
                                     @NonNull SavedStateHandle savedStateHandle) {
        super(application);
        mSavedStateHandler = savedStateHandle;

//        mRepository = ((BasicApp) application).getRepository();
        mRepository = DataRepository.getInstance(AppDatabase.getInstance(application.getBaseContext()));

        // Use the savedStateHandle.getLiveData() as the input to switchMap,
        // allowing us to recalculate what LiveData to get from the DataRepository
        // based on what query the user has entered
        mConversations = Transformations.switchMap(
                savedStateHandle.getLiveData("QUERY", null),
                (Function<CharSequence, LiveData<List<ConversationEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return mRepository.getConversations();
                    }
                    return null;//mRepository.searchConversations("*" + query + "*");
                });
    }

    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<ConversationEntity>> getConversations() {
        return mRepository.getConversations();
    }

    public void loadConversations(){
        mRepository.loadConversations();
    }


    public long insertConversation(ConversationEntity ce){
        return mRepository.insertConversation(ce);
    }

    public void updateConversation(ConversationEntity ce){
        mRepository.updateConversation(ce);
    }

    public void deleteConversation(long conversationId){
        mRepository.deleteConversation(conversationId);
    }

    public void deleteConversation(ConversationEntity ce){
        mRepository.deleteConversation(ce);
    }

    public static ChatbotMultiCard getJson(String url, MessageEntity me){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);
        try{
            service.getMessageContent().subscribe(new Consumer<ChatbotMultiCard>() {
                @Override
                public void accept(ChatbotMultiCard chatbotMultiCardBaseResult) throws Exception{
                    Log.i("Junwang", "retrofit accept");
                    if(chatbotMultiCardBaseResult.getCode() == 200){
                        Log.i("Junwang", "getCode() == 200");
                        MultiCardChatbotMsg mcm = chatbotMultiCardBaseResult.getdata();
                        if(mcm != null){
                            Log.i("Junwang", "cbc != null");
//                            MultiCardChatbotMsg mcm = cbc.getdata();
                            if(mcm != null){
                                me.setMessageType(mcm.getMsgType());
                                switch (mcm.getMsgType()){
                                    case MessageConstants.CONTENT_TYPE_TEXT:
                                        me.setContent(mcm.getPlainText());
                                        break;
                                    case MessageConstants.CONTENT_TYPE_IMAGE:
                                    case MessageConstants.CONTENT_TYPE_AUDIO:
                                    case MessageConstants.CONTENT_TYPE_VIDEO:
                                        CardContent[] cc = mcm.getGeneralPurposeCardCarousel().getContent();
                                        String mediaUrl = cc[0].getMedia().getMediaUrl();
                                        String thumbnailUrl = cc[0].getMedia().getThumbnailUrl();
                                        me.setAttachmentPath(mediaUrl);
                                        me.setThumbnailPath(thumbnailUrl);
                                        break;
                                    case MessageConstants.CONTENT_TYPE_MULTI_CARD:
                                    case MessageConstants.CONTENT_TYPE_SINGLE_CARD:
                                        String jsonString = new Gson().toJson(mcm);
                                        Log.i("Junwang", "msg content mcmJsonString="+jsonString);
                                        me.setContent(jsonString);
                                        break;
                                    default:
                                        me.setContent(mcm.toString());
                                        break;
                                }
                            }
                        }
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.i("Junwang", "getJson exception "+throwable.toString());
                    me.setMessageType(MessageConstants.CONTENT_TYPE_TEXT);
                    me.setContent(null);
                    throwable.printStackTrace();
                }
            });
        }catch(Exception e){
            me.setMessageType(MessageConstants.CONTENT_TYPE_TEXT);
            me.setContent(null);
            Log.i("Junwang", "getJson exception "+e.toString());
        }
        return null;
    }

    public MessageEntity saveLocationMsg(Context context, String content, String destination, boolean isReceived, int messageType, LocationData locationData){
        long convId = DataRepository.getInstance(AppDatabase.getInstance(context)).getConversationId(destination);
        Log.i("Junwang", "addMessage query conversation Id = "+ convId);

        final long time = System.currentTimeMillis();
        ConversationEntity ce = new ConversationEntity();
        ce.setLastTimestamp(time);
        ce.setNormalizedDestination(destination);
        if(convId == 0){
            convId = insertConversation(ce);
        }
        ce.setId(convId);

        MessageEntity me = new MessageEntity();
        me.setConversationId(convId);
        if(isReceived){
            getJson(content, me);
            me.setDirection(MessageConstants.DIRECTION_IN);
            me.setReceivedTimeStamp(time);
            me.setRead(1);
        }else {
            if(content != null){
                me.setContent(content);
            }
            me.setDirection(MessageConstants.DIRECTION_OUT);
            me.setSentTimestamp(time);
            me.setMessageType(messageType);
            me.setRead(0);
        }
        me.setLocationData(locationData);
        me.setAttachmentPath(null);
        me.setMessageStatus(isReceived ? MessageConstants.BUGLE_STATUS_INCOMING_COMPLETE : MessageConstants.BUGLE_STATUS_OUTGOING_SENDING);
        long messageId = mRepository.insertMessage(me);
        Log.i("Junwang", "insert messageId="+messageId);
        me.setId(messageId);
        ce.setLatestMessageId(messageId);
        ce.setSnippetText(me.generateSnippetText());
        if(isReceived) {
            ce.setUnreadCount(getUnreadCount(convId));
        }
        updateConversation(ce);
        if(isReceived){
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setUri(destination);
            userInfoEntity.setName("中国移动");
//            userInfoEntity.setName("新华社");
            String menu = "{\n" +
                    "\"menu\":{\"entries\":[{\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"现场云\",\n" +
                    "                        \"entries\":[\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"现场云直播\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/home/17024\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"中纪委专刊\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/share/9264023\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"区县融媒体\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/share/9332289\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"民族品牌\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/theme/18052\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"央企服务\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhyz.vizen.cn/\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            }\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"新华99\",\n" +
                    "                        \"entries\":[\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"地标特产\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"http://testxhs.supermms.cn/H5/productList.html\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"厂直优品\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"http://testxhs.supermms.cn/H5/mostBest.html\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            }\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"我的\",\n" +
                    "                        \"entries\":[\n" +
                    "\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "}";
//            userInfoEntity.setMenu(menu);
            userInfoEntity.setPortrait("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/51/5d89bdce9bf79.jpg");
//            userInfoEntity.setPortrait("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/51/xhs.png");
            mRepository.insertUserInfo(userInfoEntity);
        }
//        loadConversations();
        return me;
    }

    public MessageEntity saveMsg(Context context, String content, String destination, boolean isReceived, String attachmentpath, int messageType){
        long convId = DataRepository.getInstance(AppDatabase.getInstance(context)).getConversationId(destination);
        Log.i("Junwang", "addMessage query conversation Id = "+ convId);

        final long time = System.currentTimeMillis();
        ConversationEntity ce = new ConversationEntity();
        ce.setLastTimestamp(time);
        ce.setNormalizedDestination(destination);
        if(convId == 0){
            convId = insertConversation(ce);
        }
        ce.setId(convId);

        MessageEntity me = new MessageEntity();
        me.setConversationId(convId);
        if(isReceived){
            getJson(content, me);
            me.setDirection(MessageConstants.DIRECTION_IN);
            me.setReceivedTimeStamp(time);
            me.setRead(1);
        }else {
            if(content != null){
                me.setContent(content);
            }
            me.setDirection(MessageConstants.DIRECTION_OUT);
            me.setSentTimestamp(time);
            me.setMessageType(messageType);
            me.setRead(0);
        }
        if(attachmentpath != null){
            me.setAttachmentPath(attachmentpath);
            me.setThumbnailPath(attachmentpath);
        }else if(isReceived
                &&(me.getMessageType() == MessageConstants.CONTENT_TYPE_IMAGE
                || me.getMessageType() == MessageConstants.CONTENT_TYPE_AUDIO
                || me.getMessageType() == MessageConstants.CONTENT_TYPE_VIDEO)){
            String thumbnailurl = me.getThumbnailPath();
            Log.i("Junwang", "thumbnailurl="+thumbnailurl);
            String originalThumbnailFileName = thumbnailurl.substring(thumbnailurl.lastIndexOf("/")+1);
            String saveThumbnailFileName = "thumbnail"+time+"_"+originalThumbnailFileName;
            String thumbnailSavedPath = context.getFilesDir().toString() +"/"+ saveThumbnailFileName;
            FileUtils.downLoad(context, thumbnailurl, saveThumbnailFileName);
            me.setThumbnailPath(thumbnailSavedPath);

            String attachmenturl = me.getAttachmentPath();
            String originalFileName = attachmenturl.substring(attachmenturl.lastIndexOf("/")+1);
            String saveFileName = time+"_"+originalFileName;
            String attachmentSavedPath = context.getFilesDir().toString() +"/"+ saveFileName;
            FileUtils.downLoad(context, attachmenturl, saveFileName);
            me.setAttachmentPath(attachmentSavedPath);

        }else{
            me.setAttachmentPath(null);
        }
        me.setLocationData(null);
        me.setMessageStatus(isReceived ? MessageConstants.BUGLE_STATUS_INCOMING_COMPLETE : MessageConstants.BUGLE_STATUS_OUTGOING_SENDING);
        long messageId = mRepository.insertMessage(me);
        Log.i("Junwang", "insert messageId="+messageId);
        me.setId(messageId);
        ce.setLatestMessageId(messageId);
        ce.setSnippetText(me.generateSnippetText());
        if(isReceived) {
            ce.setUnreadCount(getUnreadCount(convId));
        }
        updateConversation(ce);
        if(isReceived){
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setUri(destination);
//            userInfoEntity.setName("中国移动");
            userInfoEntity.setName("新华社");
            String menu = "{\n" +
                    "\"menu\":{\"entries\":[{\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"现场云\",\n" +
                    "                        \"entries\":[\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"现场云直播\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/home/17024\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"中纪委专刊\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/share/9264023\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"区县融媒体\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/share/9332289\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"民族品牌\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhpfmapi.zhongguowangshi.com/vh512/theme/18052\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"央企服务\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"https://xhyz.vizen.cn/\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            }\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"新华99\",\n" +
                    "                        \"entries\":[\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"地标特产\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"http://testxhs.supermms.cn/H5/productList.html\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            },\n" +
                    "                            {\n" +
                    "                                \"reply\":{\n" +
                    "                                    \"displayText\":\"厂直优品\",\n" +
                    "                                    \"postback\":{\n" +
                    "                                        \"data\":\"http://testxhs.supermms.cn/H5/mostBest.html\"\n" +
                    "                                    }\n" +
                    "                                }\n" +
                    "                            }\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"menu\":{\n" +
                    "                        \"displayText\":\"我的\",\n" +
                    "                        \"entries\":[\n" +
                    "\n" +
                    "                        ]\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        }\n" +
                    "}";
            userInfoEntity.setMenu(menu);
//            userInfoEntity.setPortrait("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/51/5d89bdce9bf79.jpg");
            userInfoEntity.setPortrait("http://sms-agent.oss-cn-hangzhou.aliyuncs.com/sms_agent_temp/51/xhs.png");
            mRepository.insertUserInfo(userInfoEntity);
        }
//        loadConversations();
        return me;
    }

    //need to implement
    public int getUnreadCount(final long conversationId){
        return mRepository.getUnreadCount(conversationId);
    }

    public void updateMessageReadStatus(){

    }

    public LiveData<MessageEntity> getLastMessage(final long conversationId){
        return mRepository.getLastMessage(conversationId);
    }

    public void removeConversation(Conversation conversation){}
}
