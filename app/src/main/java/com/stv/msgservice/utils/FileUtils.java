package com.stv.msgservice.utils;

import android.content.Context;
import android.util.Log;

import com.jimi_wu.easyrxretrofit.RetrofitManager;
import com.jimi_wu.easyrxretrofit.observer.DownLoadObserver;
import com.jimi_wu.easyrxretrofit.observer.UploadObserver;
import com.jimi_wu.easyrxretrofit.upload.UploadParam;
import com.stv.msgservice.datamodel.constants.MessageConstants;
import com.stv.msgservice.datamodel.network.FileBean;
import com.stv.msgservice.datamodel.network.ResultBean;
import com.stv.msgservice.datamodel.network.UploadFileCallback;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FileUtils {
    /**
     * 文件下载
     */
    public static void downLoad(Context context, String url, String savedName) {
        RetrofitManager
                .download(url, context.getFilesDir().toString(), savedName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownLoadObserver() {
                    @Override
                    public void _onNext(String result) {
                        Log.i("retrofit", "onNext=======>" + result);
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress=======>" + percent);

                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.i("retrofit", "onProgress=======>" + e.getMessage());
                    }
                });
    }

    /**
     * 单图上传
     */
    public static void upload(File file, String url, UploadFileCallback callback) {
        RetrofitManager
                .uploadFile(url, new UploadParam("upload", file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                .subscribe(new UploadObserver<ResultBean<FileBean>>() {
                    String url;
                    @Override
                    public void _onNext(ResultBean<FileBean> fileBeanResultBean) {
//                        url = fileBeanResultBean.getData().getUrl();
                        Log.i("retrofit", "onNext=======>url:");
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress======>" + percent);
                        if(percent.intValue() == 100){
                            if(callback != null){
                                callback.onSuccess(url);
                            }
                        }
                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                        Log.i("retrofit", "onProgress======>" + "上传中:" + uploaded + "/" + sumLength);
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.i("retrofit", "onError======>" + e.getMessage());
                        if(callback != null){
                            callback.onFail(-1);
                        }
                    }
                });
    }

    /**
     * 单图上传
     */
    public static void uploadChatbotFile(File file, String url, UploadFileCallback callback) {
        RetrofitManager
                .uploadFile(url, new UploadParam("upload", file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()/*Schedulers.io()*/)
                .subscribe(new Consumer<Object>(){
                    @Override
                    public void accept(Object o) throws Exception {
                        if(callback != null){
                            Log.i("Junwang", "FileUtils uploadChatbotFile Successful.");
//                            ResultBean<String> result = (ResultBean<String>)o;
//                            if(result != null){
//                                callback.onSuccess(result.getData());
//                            }else{
//                                callback.onSuccess(null);
//                            }
                            callback.onSuccess(null);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if(callback != null){
                            callback.onFail(-1);
                        }
                        Log.e("Junwang", "uploadChatbotFile "+throwable.toString());
                    }
                });
    }


    /**
     * 多图上传
     */
    public static void uploads(ArrayList<File> files) {
        ArrayList<UploadParam> uploadParams = new ArrayList<>(files.size());
        for (File file : files) {
            uploadParams.add(new UploadParam("upload", file));
        }
        RetrofitManager
                .uploadFile(MessageConstants.UPLOADS_URL,
                        uploadParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UploadObserver<ResultBean<ArrayList<FileBean>>>() {

                    @Override
                    public void _onNext(ResultBean<ArrayList<FileBean>> arrayListResultBean) {
                        ArrayList<FileBean> fileBeans = arrayListResultBean.getData();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (FileBean fileBean : fileBeans) {
                            stringBuilder.append("上传成功:" + fileBean.getUrl() + "\n");
                        }
                        Log.d("retrofit", "onNext=======>" + stringBuilder);
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress=======>" + percent);
                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                        Log.i("retrofit", "onProgress======>" + "上传中:" + uploaded + "/" + sumLength);
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.d("retrofit", "onError======>" + e.getMessage());
                    }
                });
    }
}
