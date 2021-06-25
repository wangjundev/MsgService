package com.stv.msgservice.datamodel.network;

public interface UploadFileCallback {
    void onSuccess(String url);

    void onFail(int errorCode);
}
