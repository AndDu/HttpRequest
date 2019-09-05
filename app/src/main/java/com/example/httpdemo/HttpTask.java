package com.example.httpdemo;


import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class HttpTask<T> implements Runnable, Delayed {

    private IHttpRequest iHttpRequest;

    public HttpTask(String url, T requestData, IHttpRequest iHttpRequest, CallBackListener callBackListener) {
        this.iHttpRequest = iHttpRequest;
        iHttpRequest.setUrl(url);
        iHttpRequest.setListener(callBackListener);
        String content = JSON.toJSONString(requestData);
        try {
            iHttpRequest.setData(content.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {

        try {
            iHttpRequest.excute();
        } catch (Exception e) {

            Log.e("run: ", "重试");
            ThreadPoolManager.getInstance().addDelayTask(this);
        }

    }


    private long delayTime;
    private int retryCount;

    public void setDelayTime(long delayTime) {
        this.delayTime = System.currentTimeMillis() + delayTime;
    }

    public long getDelayTime() {
        return delayTime;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    @Override
    public long getDelay(TimeUnit timeUnit) {
        return timeUnit.convert(this.delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed delayed) {
        return 0;
    }
}
