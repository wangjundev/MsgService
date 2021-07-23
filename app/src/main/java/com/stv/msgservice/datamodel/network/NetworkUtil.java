package com.stv.msgservice.datamodel.network;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import com.baronzhang.retrofit2.converter.FastJsonConverterFactory;
import com.cjt2325.cameralibrary.util.LogUtil;
import com.google.gson.Gson;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.stv.msgservice.R;
import com.stv.msgservice.datamodel.TerminalInfo.DeviceIdUtil;
import com.stv.msgservice.datamodel.TerminalInfo.TerminalInfo;
import com.stv.msgservice.datamodel.chatbotinfo.Botinfo;
import com.stv.msgservice.datamodel.chatbotinfo.CategoryList;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotInfo;
import com.stv.msgservice.datamodel.chatbotinfo.Media;
import com.stv.msgservice.datamodel.chatbotinfo.MediaEntry;
import com.stv.msgservice.datamodel.chatbotinfo.MediaList;
import com.stv.msgservice.datamodel.chatbotinfo.OrgDetails;
import com.stv.msgservice.datamodel.chatbotinfo.OrgName;
import com.stv.msgservice.datamodel.chatbotinfo.Pcc;
import com.stv.msgservice.datamodel.chatbotinfo.PersistentMenu;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.database.AppDatabase;
import com.stv.msgservice.datamodel.database.entity.ConversationEntity;
import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.database.entity.UserInfoEntity;
import com.stv.msgservice.datamodel.datarepository.DataRepository;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotFile;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBean;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBody;
import com.stv.msgservice.datamodel.viewmodel.MessageViewModel;
import com.stv.msgservice.ui.conversation.ConversationActivity;
import com.stv.msgservice.utils.FileUtils;
import com.stv.msgservice.utils.VideoUtil;
import com.thoughtworks.xstream.XStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.core.app.NotificationCompat;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static androidx.core.app.NotificationCompat.CATEGORY_MESSAGE;
import static androidx.core.app.NotificationCompat.DEFAULT_ALL;

public class NetworkUtil {
//    private DataRepository mRepository;

    public static String getPostBodyJson(Context context, String keyword, String orderNo, String chatbotId){
        HashMap<String,String> paramsMap=new HashMap<>();
        if(keyword != null) {
            paramsMap.put("q", keyword);
        }
        if(orderNo != null){
            paramsMap.put("orderNo",orderNo);
        }
        if(chatbotId != null){
            paramsMap.put("id", chatbotId);
        }
        paramsMap.put("client_vendor", TerminalInfo.getClientVendor());
        paramsMap.put("client_version", TerminalInfo.getTerminalSoftwareVersion());
//        paramsMap.put("client_serial", TerminalInfo.getMEID(context)+"_"+TerminalInfo.getVerName(context));
        String androidId = TerminalInfo.getAndroidId(context);
        String serial = TerminalInfo.getSERIAL();
        Log.i("Junwang", "androidId="+androidId+",serial="+serial);
        String deviceId = DeviceIdUtil.getDeviceId(context);
        Log.i("Junwang", "deviceId="+deviceId);
        paramsMap.put("client_serial", deviceId/*androidId+"_"+TerminalInfo.getSERIAL()+"_"+TerminalInfo.getVerName(context)*/);
        Gson gson=new Gson();
        String strEntity = gson.toJson(paramsMap);
        Log.i("Junwang", "strEntity="+strEntity);
        return strEntity;
    }

    public static String getLocalPhoneNo(Context context){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("5GMsg", Context.MODE_PRIVATE);
        String localPhoneNo = sharedPreferences.getString("localPhoneNo", null);
        if(localPhoneNo == null){
            Log.i("Junwang", "获取不到本机号码");
//            SmsManager smsManager = SmsManager.getDefault();
//            smsManager.sendTextMessage("+8615958120627", null, "abcdefg", null, null);
        }
        return localPhoneNo;
    }

    private static void saveLocalPhoneNo(Context context, String phoneNo){
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences("5GMsg", Context.MODE_PRIVATE);
        String localPhoneNo = sharedPreferences.getString("localPhoneNo", null);
        if(localPhoneNo == null) {
            sharedPreferences.edit()
                    .putString("localPhoneNo", phoneNo)
                    .apply();
        }
    }

    @SuppressLint("CheckResult")
    public static ChatbotMessageBody getXml(Context context, String orderNo, String domain){
        DataRepository mRepository = DataRepository.getInstance(AppDatabase.getInstance(context));

//        int tokenIndex = domain.indexOf("/");
//        String baseUrl = domain.substring(0, tokenIndex+1);
//        String token = domain.substring(tokenIndex+1);
//        Log.i("Junwang", "getXml baseUrl="+baseUrl+", token="+token+", domain="+domain);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+domain+"/api/catherine/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(SimpleXmlConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(LenientGsonConverterFactory.create())
                .build();

        String postBody = getPostBodyJson(context, null, orderNo, null);
        Log.i("Junwang", "post body json is "+postBody);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);

        ApiService service = retrofit.create(ApiService.class);
        Observable<ResultBean<String>> response = service.getXmlMessage(body/*, token*/);
        try{
            response
                    .subscribe(new Consumer<ResultBean<String>>() {
                @SuppressLint("CheckResult")
                @Override
                public void accept(ResultBean<String> chatbotMessageBodyResultBean) throws Exception {
                    if (chatbotMessageBodyResultBean.getCode() == 0) {
                        String xmlContent = chatbotMessageBodyResultBean.getData();
                        Log.i("Junwang", "get xmlContent="+xmlContent);
                        Serializer serializer=new Persister();
                        ChatbotMessageBody chatbotMessageBody=(ChatbotMessageBody)serializer.read(ChatbotMessageBody.class, xmlContent);
                        if(chatbotMessageBody != null){
                            String sender = chatbotMessageBody.getSenderAddress();
                            Log.i("Junwang", "accept sender address = "+ sender);
                            long convId = DataRepository.getInstance(AppDatabase.getInstance(context)).getConversationId(sender);
                            Log.i("Junwang", "addMessage query conversation Id = "+ convId);
                            final long time = System.currentTimeMillis();
                            ConversationEntity ce = new ConversationEntity();
                            ce.setLastTimestamp(time);
                            ce.setSenderAddress(sender);
                            ce.setConversationID(chatbotMessageBody.getOutboundIMMessage().getConversationID());
                            if(convId == 0){
                                convId = mRepository.insertConversation(ce);
                            }
                            ce.setId(convId);

                            MessageEntity me = new MessageEntity();
                            me.setConversationId(convId);
                            me.setDirection(MessageConstants.DIRECTION_IN);
                            me.setReceivedTimeStamp(time);
                            me.setRead(1);
                            me.setDomain(domain);

                            String contentType = chatbotMessageBody.getOutboundIMMessage().getContentType();
                            String bodyText = chatbotMessageBody.getOutboundIMMessage().getBodyText();
                            String destination = chatbotMessageBody.getDestinationAddress();
                            Log.i("Junwang", "received xml destination="+destination);
//                            if(destination != null && destination.startsWith("tel:")){
//                                ce.setDestinationAddress(destination.substring(4));
//                            }else {
//                                ce.setDestinationAddress(destination);
//                            }
                            me.setContributionID(chatbotMessageBody.getOutboundIMMessage().getContributionID());
                            me.setConversationID(chatbotMessageBody.getOutboundIMMessage().getConversationID());
                            me.setMessageId(chatbotMessageBody.getOutboundIMMessage().getMessageId());
                            LogUtil.i("Junwang", "getMessageBody address="+chatbotMessageBody.getAddress()+", bodyText="+bodyText
                                    +", contentType="+contentType);
                            //卡片
                            if("application/vnd.gsma.botmessage.v1.0+json".equals(contentType)){
                                LogUtil.i("Junwang", "卡片消息");
                                if(bodyText.startsWith("{") || bodyText.startsWith("\n{")){
                                    if(bodyText.indexOf("generalPurposeCardCarousel") != -1) {
                                        LogUtil.i("Junwang", "multi card chatbot message");
                                        me.setMessageType(MessageConstants.CONTENT_TYPE_MULTI_CARD);
                                    }else if(bodyText.indexOf("generalPurposeCard") != -1){
                                        LogUtil.i("Junwang", "single card chatbot message");
                                        me.setMessageType(MessageConstants.CONTENT_TYPE_SINGLE_CARD);
                                    }else{
                                        LogUtil.i("Junwang", "不能被识别的卡片消息");
                                    }
                                    me.setContent(bodyText);
                                }
                            }
                            //带建议回复的消息
//                        else if(/*bodyText.startsWith("--next") || bodyText.startsWith("\n--next")*/"multipart/mixed; boundary=\"next\"".equals(contentType)){
                            else if(contentType != null && contentType.startsWith("multipart/mixed;")){
                                Log.i("Junwang", "start parse message with suggestion");
                                me.setContent(bodyText);
                                int index = contentType.indexOf("boundary=");
                                int lastIndex = contentType.lastIndexOf("\"");
                                String boundary = "--"+contentType.substring(index+10, lastIndex);
                                Log.i("Junwang", "boundary="+boundary);
                                String[] messageContent = bodyText.split("--next");
                                ArrayList<ChatbotMessageBean> cmbList = new ArrayList<>();
                                for(int i=0; i<messageContent.length; i++){
                                    BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageContent[i].getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
                                    String line;
                                    StringBuffer strbuf=new StringBuffer();
                                    ChatbotMessageBean cmb = new ChatbotMessageBean(null, null, null);
                                    try{
                                        while ( (line = br.readLine()) != null ) {
                                            if(!line.trim().equals("")){
                                                if(line.startsWith("Content-Type: ")){
                                                    cmb.setContent_type(line.substring(14));
                                                }else if(line.startsWith("Content-Length: ")){
                                                    cmb.setContent_length(line.substring(16));
                                                }else if(line.startsWith("Content-Disposition:")){

                                                }else{
                                                    strbuf.append(line+"\r\n");
                                                }
                                            }
                                        }
                                        if(strbuf != null) {
                                            cmb.setContent_text(strbuf.toString());
                                        }
                                        cmbList.add(cmb);
                                    }catch (Exception e){
                                        LogUtil.e("Junwang", "parse ChatbotMessageBean exception "+e.toString());
                                    }
                                }
                                String cmbText = null;
                                String suggestionJson = null;
                                for(ChatbotMessageBean bean : cmbList){
                                    if("text/plain".equals(bean.getContent_type())){
                                        LogUtil.i("Junwang", "start parse plain text + suggestions RCS.");
                                        me.setMessageType(MessageConstants.CONTENT_TYPE_TEXT_WITH_SUGGESTION);
                                        cmbText = bean.getContent_text();
                                    }else if("application/vnd.gsma.rcs-ft-http+xml".equals(bean.getContent_type())){
                                        LogUtil.i("Junwang", "start parse RCS file + suggestions RCS.");
                                        try{
                                            Serializer se=new Persister();
                                            ChatbotFile file=(ChatbotFile )se.read(ChatbotFile.class,bean.getContent_text());
                                            for(int i=0; i<file.getFileInfo().size(); i++) {
                                                LogUtil.i("Junwang", "parse RCS file content-type=" + file.getFileInfo().get(i).getContent_type()
                                                        +", file-size="+file.getFileInfo().get(i).getFileSize()
                                                        +", file url="+file.getFileInfo().get(i).getData().getUrl());
                                                if("image/jpeg".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "image/gif".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "image/bmp".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "image/png".equals(file.getFileInfo().get(i).getContent_type())){
                                                    LogUtil.i("Junwang", "parse image + suggestions RCS.");
                                                    me.setMessageType(MessageConstants.CONTENT_TYPE_IMAGE_WITH_SUGGESTION);
                                                }else if("audio/amr".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "audio/mp3".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "audio/aac".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "audio/wav".equals(file.getFileInfo().get(i).getContent_type())){
                                                    LogUtil.i("Junwang", "parse audio + suggestions RCS.");
                                                    me.setMessageType(MessageConstants.CONTENT_TYPE_AUDIO_WITH_SUGGESTION);
                                                }else if("video/mp4".equals(file.getFileInfo().get(i).getContent_type())
                                                        || "video/3gpp".equals(file.getFileInfo().get(i).getContent_type())){
                                                    LogUtil.i("Junwang", "parse video + suggestions RCS.");
                                                    me.setMessageType(MessageConstants.CONTENT_TYPE_VIDEO_WITH_SUGGESTION);
                                                }else{
                                                    LogUtil.i("Junwang", "parse unknown file type + suggestion RCS.");
                                                }
                                            }
                                        }catch (Exception e){
                                            LogUtil.e("Junwang", "parse RCS file format exception "+e.toString());
                                        }
                                    }
                                    else if("application/vnd.gsma.botmessage.v1.0+json".equals(bean.getContent_type())){
                                        LogUtil.i("Junwang", "start parse suggestions.");
                                        String text = bean.getContent_text();
                                        Log.i("Junwang", "text = "+text);
                                        if(text.startsWith("{")){
                                            if(text.indexOf("generalPurposeCardCarousel") != -1) {
                                                LogUtil.i("Junwang", "multi card chatbot message + suggestion RCS");
                                                me.setMessageType(MessageConstants.CONTENT_TYPE_MULTI_CARD_WITH_SUGGESTION);
                                            }else if(text.indexOf("generalPurposeCard") != -1){
                                                LogUtil.i("Junwang", "single card chatbot message+ suggestion RCS");
                                                me.setMessageType(MessageConstants.CONTENT_TYPE_SINGLE_CARD_WITH_SUGGESTION);
                                            }else{
                                                LogUtil.i("Junwang", "不能被识别的卡片消息");
                                            }
                                        }
//                                    suggestionJson = bean.getContent_text();
                                    }
                                }
                            }else if("text/plain".equals(contentType)){
                                me.setMessageType(MessageConstants.CONTENT_TYPE_TEXT);
                                me.setContent(bodyText);
                            }else if("application/vnd.gsma.rcs-ft-http+xml".equals(contentType)){
                                try{
                                    Serializer se=new Persister();
                                    ChatbotFile file=(ChatbotFile )se.read(ChatbotFile.class, bodyText);
                                    String fileType = file.getFileInfo().get(0).getContent_type();
                                    String fileContentType;
                                    String thumbnail = null;
                                    String mediaUrl = null;
                                    for(int i=0; i<file.getFileInfo().size(); i++){
                                        fileType = file.getFileInfo().get(i).getType();
                                        fileContentType = file.getFileInfo().get(i).getContent_type();
                                        if("thumbnail".equals(fileType)){
                                            thumbnail = file.getFileInfo().get(i).getData().getUrl();
                                            me.setThumbnailPath(thumbnail);
                                        }else if("file".equals(fileType)){
                                            mediaUrl = file.getFileInfo().get(i).getData().getUrl();
                                            Log.i("Junwang", "parsed mediaUrl="+mediaUrl);
                                            me.setAttachmentPath(mediaUrl);
                                            if(fileContentType != null && fileContentType.startsWith("image/")){
                                                me.setMessageType(MessageConstants.CONTENT_TYPE_IMAGE);
                                            }else if(fileContentType != null && fileContentType.startsWith("audio/")){
                                                me.setMessageType(MessageConstants.CONTENT_TYPE_AUDIO);
                                            }else if(fileContentType != null && fileContentType.startsWith("video/")){
                                                me.setMessageType(MessageConstants.CONTENT_TYPE_VIDEO);
                                            }
                                        }
                                    }
                                }catch (Exception e){
                                    Log.i("Junwang", "parse RCS plain file format exception "+e.toString());
                                }

                            }else{
                                Log.i("Junwang", "error msg type.");
                                me.setMessageType(MessageConstants.CONTENT_TYPE_TEXT);
                                me.setContent(bodyText);
                            }

                            if(me.getMessageType() == MessageConstants.CONTENT_TYPE_IMAGE
                                    || me.getMessageType() == MessageConstants.CONTENT_TYPE_AUDIO
                                    || me.getMessageType() == MessageConstants.CONTENT_TYPE_VIDEO){
                                String attachmenturl = me.getAttachmentPath();
                                String originalFileName = attachmenturl.substring(attachmenturl.lastIndexOf("/")+1);
                                String saveFileName = time+"_"+originalFileName;
                                String attachmentSavedPath = context.getFilesDir().toString() +"/"+ saveFileName;
                                FileUtils.downLoad(context, attachmenturl, saveFileName);
                                me.setAttachmentPath(attachmentSavedPath);

                                String thumbnailurl = me.getThumbnailPath();
                                Log.i("Junwang", "thumbnailurl="+thumbnailurl);
                                if(thumbnailurl == null){
                                    if(me.getMessageType() == MessageConstants.CONTENT_TYPE_VIDEO){
                                        Bitmap b = VideoUtil.getVideoThumb(attachmenturl);
                                        if(b != null){
                                            thumbnailurl = VideoUtil.bitmap2File(context, b, "thumb_"+ SystemClock.currentThreadTimeMillis());
                                            me.setThumbnailPath(thumbnailurl);
                                        }
                                    }else if(me.getMessageType() == MessageConstants.CONTENT_TYPE_IMAGE){
                                        me.setThumbnailPath(me.getAttachmentPath());
                                    }
                                }else{
                                    String originalThumbnailFileName = thumbnailurl.substring(thumbnailurl.lastIndexOf("/")+1);
                                    String saveThumbnailFileName = "thumbnail"+time+"_"+originalThumbnailFileName;
                                    String thumbnailSavedPath = context.getFilesDir().toString() +"/"+ saveThumbnailFileName;
                                    FileUtils.downLoad(context, thumbnailurl, saveThumbnailFileName);
                                    me.setThumbnailPath(thumbnailSavedPath);
                                }
                            }else{
                                me.setAttachmentPath(null);
                            }
                            me.setLocationData(null);
                            me.setMessageStatus(MessageConstants.BUGLE_STATUS_INCOMING_COMPLETE);
                            long messageId = mRepository.insertMessage(me);
                            Log.i("Junwang", "insert messageId="+messageId);
                            me.setId(messageId);
                            ce.setLatestMessageId(messageId);
                            ce.setSnippetText(me.generateSnippetText());
                            ce.setUnreadCount(mRepository.getUnreadCount(convId));
                            Log.i("Junwang", "update ce sender address = "+ce.getSenderAddress());
                            mRepository.updateConversation(ce);
                            if(/*true*/false){
                                UserInfoEntity userInfoEntity = new UserInfoEntity();
//            userInfoEntity.setUri(destination);
                                userInfoEntity.setUri(ce.getSenderAddress());
//                            userInfoEntity.setName("中国移动");
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
                            }else{
                                getChatbotInfo(mRepository, context, ce.getSenderAddress());
                            }
                            sendStatusReport(mRepository, context, me, ce.getSenderAddress(), ce.getDestinationAddress(), me.getMessageId(), MessageConstants.DELIVEREDTOTERMINAL);
                            showNotification(context, ce);
                            saveLocalPhoneNo(context, destination);
                        }
                    } else {
                        Log.i("Junwang", "get message failed, reson = " + chatbotMessageBodyResultBean.getMsg());
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    Log.i("Junwang", "getxml Throwable "+throwable.toString());
                }
            });

        }catch (Exception e){
            Log.i("Junwang", "getxml exception "+e.toString());
        }
        return null;
    }

    public static void showNotification(Context context, ConversationEntity ce){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationUtils utils = new NotificationUtils(context);
            Intent intent = new Intent(context, ConversationActivity.class);
            intent.putExtra("conversation", ce);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)ce.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
            utils.sendNotification(1, ce.getSnippetText(), ce.getSnippetText(), R.mipmap.ic_launcher_5gmsg, pendingIntent);
        }else{
            Notification.Builder mBuilder = new Notification.Builder(context)
                    .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setSmallIcon(R.mipmap.ic_launcher_5gmsg)
                    .setContentTitle(ce.getSnippetText())
                    .setContentText(ce.getSnippetText())
                    .setAutoCancel(true);
            Intent resultIntent = new Intent(context, ConversationActivity.class);
            resultIntent.putExtra("conversation", ce);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //将该Activity添加为栈顶
            stackBuilder.addParentStack(ConversationActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    public static void showNotification(Context context, String title, String content){
        NotificationUtil.initNotificationManager(context);
        Intent intent = new Intent(context, ConversationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, /*channelId*/"5gMsgNotification")
                .setSmallIcon(R.mipmap.ic_launcher_5gmsg)
//                .setAutoCancel(true)
                .setCategory(CATEGORY_MESSAGE)
                .setDefaults(DEFAULT_ALL);
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
        builder.setContentText(content);

        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        manager.notify(1, notification);
//        Notification notification1 = new NotificationCompat.Builder(context, "chat")
//                        .setContentIntent(pendingIntent)
//                        .build();
        //        manager.notify();



    }

    public static void getChatbotInfo(DataRepository mRepository, Context context, String chatbotId){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MessageConstants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(FastJsonConverterFactory.create())
                .build();

        String postBody = getPostBodyJson(context, null, null, chatbotId);
        Log.i("Junwang", "post body json is "+postBody);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), postBody);

        ApiService service = retrofit.create(ApiService.class);
        try {
            service.getChatbotInfo(body)
                    .subscribe(new Consumer<ChatbotInfo>() {
                        @Override
                        public void accept(ChatbotInfo chatbotInfo) throws Exception {
                            if(chatbotInfo != null){
                                UserInfoEntity userInfoEntity = new UserInfoEntity();
                                userInfoEntity.setUri(chatbotId);
                                PersistentMenu menu = chatbotInfo.getPersistent_menu();
                                if(menu != null){
                                    Log.i("Junwang", "menu = "+menu.getMenu().toString());
                                    String menuJson = new Gson().toJson(menu);
                                    userInfoEntity.setMenu(menuJson);
                                }else{
                                    userInfoEntity.setMenu(null);
                                }

                                Botinfo botinfo = chatbotInfo.getBotinfo();
                                if(botinfo != null){
                                    Pcc pcc = botinfo.getPcc();
                                    if(pcc != null){
                                        OrgDetails orgDetails = pcc.getOrg_details();
                                        if(orgDetails != null){
                                            MediaList mediaList = orgDetails.getMedia_list();
                                            if(mediaList != null){
                                                List<MediaEntry> mediaEntries = mediaList.getMedia_entry();
                                                if(mediaEntries != null && mediaEntries.size() > 0){
                                                    MediaEntry mediaEntry = mediaEntries.get(0);
                                                    if(mediaEntry != null){
                                                        Media media = mediaEntry.getMedia();
                                                        if(media != null){
                                                            userInfoEntity.setPortrait(media.getMedia_url());
                                                        }
                                                    }
                                                }
                                            }

                                            List<OrgName> orgNameList = orgDetails.getOrg_name();
                                            if(orgNameList != null && orgNameList.size() > 0){
                                                OrgName orgName = orgNameList.get(0);
                                                if(orgName != null)
                                                    userInfoEntity.setName(orgName.getDisplay_name());
                                                Log.i("Junwang", "search chatbotInfo id="+chatbotId+", name="+orgName.getDisplay_name());
                                            }

                                            CategoryList categoryList = orgDetails.getCategory_list();
                                            if(categoryList != null){
                                                List<String> entry = categoryList.getCategory_entry();
                                                if(entry != null && entry.size() > 0){
                                                    userInfoEntity.setCategory(entry.get(0));
                                                }
                                            }

                                            userInfoEntity.setDescription(orgDetails.getOrg_description());
                                        }
                                    }
                                }
                                mRepository.insertUserInfo(userInfoEntity);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Log.e("Junwang", "get chatbot info exception "+throwable.toString());
                        }
                    });
        }catch(Exception e){
            Log.e("Junwang", "get chatbot info exception "+e.toString());
        }
    }

    public static void sendStatusReport(DataRepository mRepository, Context context, MessageEntity me, String senderAddress, String destinationAddress, String messageId, String deliveryStatus){
        try{
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MessageConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiService service = retrofit.create(ApiService.class);

//            DeliveryInfo deliveryInfo = new DeliveryInfo(destinationAddress, messageId, deliveryStatus);
//            DeliveryInfoNotification deliveryInfoNotification = new DeliveryInfoNotification(deliveryInfo);
//            XStream xStream = new XStream();
//            xStream.aliasType("deliveryInfoNotification", DeliveryInfoNotification.class);
////            xStream.alias("inboundMessage", null);
//            String requestXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+xStream.toXML(deliveryInfoNotification);
//            Log.i("Junwang", "requestXml="+requestXml);

            String xmlPrefix = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<msg:deliveryInfoNotification xmlns:msg=\"urn:oma:xml:rest:netapi:messaging:1\">\n";
            String xmlSuffix = "\n</msg:deliveryInfoNotification>";
            DeliveryInfo deliveryInfo = new DeliveryInfo(destinationAddress, messageId, deliveryStatus);
//        DeliveryInfoNotification deliveryInfoNotification = new DeliveryInfoNotification(deliveryInfo);
            XStream xStream = new XStream();
            xStream.aliasType("deliveryInfo", DeliveryInfo.class);
            String requestXml = xmlPrefix+xStream.toXML(deliveryInfo)+xmlSuffix;
            Log.i("Junwang", "requestXml="+requestXml);

            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/xml;charset=UTF-8"),requestXml);

            String concatUrl = MessageViewModel.getHttpsRequestArguments(context, senderAddress, null);
            Log.i("Junwang", "concatUrl="+concatUrl);
            Flowable<Response<Void>> response = service.sendStatusReport(body, /*address*/"/5gcallback/api/catherine/DeliveryInfoNotification/"+concatUrl);
            try{
                response.subscribe(new Consumer<Response>() {
                    @Override
                    public void accept(Response responseBody) throws Exception {
                        Log.e("Junwang",  "sendStatusReport successful.");
                        me.setDeliveryStatus(MessageConstants.DELIVEREDTOTERMINAL);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("Junwang",  "sendStatusReport throwable "+throwable.toString());
                        me.setDeliveryStatus(MessageConstants.DELIVEREDFAILED);
                    }
                });
            }catch (Exception e){
                Log.e("Junwang",  "sendStatusReport exception "+e.toString());
                me.setDeliveryStatus(MessageConstants.DELIVEREDFAILED);
            }
        }catch (Exception e){
            Log.e("Junwang",  "retrofit sendStatusReport exception "+e.toString()+"," + Thread.currentThread().getName());
            me.setDeliveryStatus(MessageConstants.DELIVEREDFAILED);
        }
        mRepository.updateMessageSendStatus(me);
    }
}
