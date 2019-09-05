package com.example.httpdemo;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonCallbackListener<T> implements CallBackListener {


    private IJsonDataListener mJsonDataListener;

    public Class<T> responseClass;
    private Handler handler = new Handler(Looper.getMainLooper());


    public JsonCallbackListener(IJsonDataListener mJsonDataListener, Class<T> responseClass) {
        this.mJsonDataListener = mJsonDataListener;
        this.responseClass = responseClass;
    }

    @Override
    public void onSuccess(InputStream inputStream) {
        String content = null;
        try {
            content = getContent(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final T t = JSON.parseObject(content, responseClass);
        handler.post(new Runnable() {
            @Override
            public void run() {
                mJsonDataListener.onSuccess(t);
            }
        });
    }

    private String getContent(InputStream inputStream) throws IOException {


        byte[] b = new byte[1024];

        ByteArrayOutputStream in = new ByteArrayOutputStream();
        int bufferSize = 0;
        while ((bufferSize = inputStream.read(b)) != -1) {
            in.write(b, 0, bufferSize);
        }
        in.flush();
        in.close();
        return in.toString();
    }

    @Override
    public void onFailure() {

    }
}
