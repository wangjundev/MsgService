package com.stv.msgservice.datamodel.network;

import com.jimi_wu.easyrxretrofit.transformer.BaseModel;

public class ResultBean<T> implements BaseModel<T> {

    private int code;

    private T data;

    private String errMsg;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean isError() {
        return code != 200;
    }

    @Override
    public String getMsg() {
        return errMsg;
    }

    @Override
    public T getResult() {
        return data;
    }


}
