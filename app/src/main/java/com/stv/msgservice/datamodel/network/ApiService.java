package com.stv.msgservice.datamodel.network;

import com.stv.msgservice.datamodel.database.entity.MessageEntity;
import com.stv.msgservice.datamodel.network.chatbot.ChatbotMultiCard;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * api商店
 */
public interface ApiService {
    @Headers({"Content-type:application/json; charset=UTF-8"})
    @POST("/api/v1/trade/HasAccount.json")
    Observable<BaseResult<MessageEntity>> createCommit(@Body RequestBody route);

        @GET("right_pic_news")    //plain text
//    @GET("xinhua99_news")     //picture
//    @GET("video")             //video
//    @GET("three_pic_news")    //multi card
//    @GET("xinhua_news")         //single card
    Observable<ChatbotMultiCard> getMessageContent();
}
