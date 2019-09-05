package com.example.httpdemo;

public interface IHttpRequest {

    void setUrl(String url);


    void setData(byte[] data);


    void setListener(CallBackListener callBackListener);


    void excute();


}
