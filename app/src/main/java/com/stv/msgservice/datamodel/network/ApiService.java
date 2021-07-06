package com.stv.msgservice.datamodel.network;

import com.google.gson.JsonObject;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotInfo;
import com.stv.msgservice.datamodel.chatbotinfo.ChatbotSearchResult;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMessageBody;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMultiCard;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * api商店
 */
public interface ApiService {
//    @Headers({"Content-type:application/xml; charset=UTF-8"})
    @POST
    Observable<Response<Void>> createCommit(@Body RequestBody route, @Url String url);

    @POST("notifications/InboundMessageNotification/{token}")
    Observable<BaseResult<FileBean>> uploadFile(@Query("token") String token);

    @Multipart
    @POST
    Observable<JsonObject> upload(@Url String url, @Part List<MultipartBody.Part> params);

//        @GET("right_pic_news")    //plain text
//    @GET("xinhua99_news")     //picture
//    @GET("video")             //video
//    @GET("three_pic_news")    //multi card
    @GET("xinhua_news")         //single card
    Observable<ChatbotMultiCard> getMessageContent();

//    @GET("xinhua_news")         //single card with suggestions
//    @GET("didi_dache")  //xml single card
//    @GET("zhibo")         //xml plain text
//    @GET("xinhua_yaowen_news")  //xml picture
//    @GET("center_pic_news")     //xml multi card
    @GET("video")             //xml video
    Observable<ChatbotMessageBody> getMessageBody();

    @POST("/5gcallback/api/catherine/queryChatbotDetail")
    Observable<ChatbotInfo> getChatbotInfo(@Body RequestBody route);

    @POST("/5gcallback/api/catherine/queryChatbotList")
    Observable<ChatbotSearchResult> searchChatbotList(@Body RequestBody route);

    @POST("send")
    Observable<ResultBean<String>> getXmlMessage(@Body RequestBody route/*, @Query("token") String token*/);

//    @POST("/5gcallback/api/catherine/DeliveryInfoNotification/{token}")
//    Flowable<Response<Void>> sendStatusReport(@Body RequestBody route, @Path("token") String token);
    @POST
    Flowable<Response<Void>> sendStatusReport(@Body RequestBody route, @Url String url);
}
