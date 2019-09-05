package com.example.httpdemo;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.DelayQueue;

public class JsonHttpRequest implements IHttpRequest {

    //http用的是socket的 ，本节用httpUrlConnection
    private String url;
    private byte[] data;
    private CallBackListener callBackListener;

    private HttpURLConnection urlConnection;


    @Override
    public void setUrl(String url) {
        this.url = url;

    }

    @Override
    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public void setListener(CallBackListener callBackListener) {
        this.callBackListener = callBackListener;


    }

    @Override
    public void excute() {

        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            if (data != null) {
                OutputStream outputStream = urlConnection.getOutputStream();

                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                bos.write(data);
                bos.flush();
                outputStream.close();
                bos.close();
            }

            if (urlConnection.getResponseCode() == 200) {
                callBackListener.onSuccess(urlConnection.getInputStream());
            }else {
                throw new RuntimeException("请求失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
