package com.example.httpdemo;

public class NeHttp {


    public static <T, M> void sendJsonRequest(String url, T requestData, Class<M> response,
                                              IJsonDataListener listener) {





        HttpTask httpTask = new HttpTask(url, requestData, new JsonHttpRequest(), new JsonCallbackListener<>(listener, response));
        ThreadPoolManager.getInstance().addDelayTask(httpTask);

    }
}
