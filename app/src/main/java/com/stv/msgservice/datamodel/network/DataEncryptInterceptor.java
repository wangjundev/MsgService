package com.stv.msgservice.datamodel.network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class DataEncryptInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //请求
        Request request = chain.request();
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");
        //随机生成AES秘钥
        String aesKey = AESUtil.generateKeyString();
        Log.i("Junwang", "aesKey="+aesKey);
        try {
            //获取未加密数据
            RequestBody oldRequestBody = request.body();
            Buffer requestBuffer = new Buffer();
            oldRequestBody.writeTo(requestBuffer);
            String oldBodyStr = requestBuffer.readUtf8();
            requestBuffer.close();

            //未加密数据用AES秘钥加密
            String  newBodyStr= AESUtil.encrypt(oldBodyStr, aesKey);
            //AES秘钥用服务端RSA公钥加密
            String key= RSAUtil.encryptByPublicKey(newBodyStr, RSAUtil.loadPublicKey("HzSantiRSAKey"));
            //构成新的request 并通过请求头发送加密后的AES秘钥
            Headers headers = request.headers();
            RequestBody newBody = RequestBody.create(mediaType, newBodyStr);
            //构造新的request
            request = request.newBuilder()
                    .headers(headers)
                    .addHeader("Device-Key", /*key*/aesKey)
                    .method(request.method(), newBody)
                    .build();
//            request = request.newBuilder().header("Content-Type", newBody.contentType()
//                    .toString()).header("Content-Length", String.valueOf(newBody.contentLength()))
//                    .method(request.method(), newBody).build();
        }catch (Exception e){
            Log.i("Junwang", "DataEncryptInterceptor exception "+e.toString());
        }
        return chain.proceed(request);
//        //响应
//        Response response = chain.proceed(request);
//        if (response.code() == 200) {
//            try {
//                //获取加密的响应数据
//                ResponseBody oldResponseBody = response.body();
//                String oldResponseBodyStr = oldResponseBody.string();
//                //加密的响应数据用AES秘钥解密
//                String newResponseBodyStr="";
//                if (!TextUtils.isEmpty(oldResponseBodyStr)){
//                    newResponseBodyStr = AESUtil.aesDecrypt(oldResponseBodyStr,aesKey);
//                }
//                oldResponseBody.close();
//                //构造新的response
//                ResponseBody newResponseBody = ResponseBody.create(mediaType, newResponseBodyStr);
//                response = response.newBuilder().body(newResponseBody).build();
//            }catch (Exception e){
//                LogUtils.d("RetrofitLog","e"+e.getMessage());
//            }finally {
//                response.close();
//            }
//        }
//        //返回
//        return response;
    }
}

