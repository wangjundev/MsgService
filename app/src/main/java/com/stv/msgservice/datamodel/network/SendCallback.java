package com.stv.msgservice.datamodel.network;

public interface SendCallback {
    void onSuccess();

    void onFail(int errorCode);
}
