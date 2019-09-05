package com.example.httpdemo;


import android.util.Log;

import java.util.Timer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池管理类
 */
public class ThreadPoolManager {

    public static ThreadPoolManager getInstance() {
        return threadPoolManager;
    }

    private ThreadPoolExecutor mThreadPoolExecutor;


    private DelayQueue<HttpTask> mDelayQueue = new DelayQueue<>();
    private static ThreadPoolManager threadPoolManager = new ThreadPoolManager();

    //添加到尾部 线程安全
    private LinkedBlockingDeque<Runnable> mQueue = new LinkedBlockingDeque<>();


    private ThreadPoolManager() {
//        ExecutorService executorService = Executors.newFixedThreadPool(3);
        mThreadPoolExecutor = new ThreadPoolExecutor(3,//核心线程
                6,//最大线程
                15, TimeUnit.SECONDS,  //超时回收非核心线程
                new ArrayBlockingQueue<Runnable>(0),
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
                        //重新添加到队列
                        addTask(runnable);
                    }
                }
        );

        mThreadPoolExecutor.execute(coreThread);
        mThreadPoolExecutor.execute(delayThread);
    }

    private void addTask(Runnable runnable) {

        if (runnable != null) {
            mQueue.add(runnable);
        }
    }

    public void addDelayTask(HttpTask runnable) {

        if (runnable != null) {
            runnable.setDelayTime(3000);
            mDelayQueue.add(runnable);
        }
    }


    //穿件核心线程，将队列中的请求拿出来，交给线程池处理
    public Runnable coreThread = new Runnable() {
        private Runnable runnable;

        @Override
        public void run() {
            while (true) {

                try {
                    runnable = mQueue.take(); //会不会阻塞？
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mThreadPoolExecutor.execute(runnable);

            }
        }
    };

    //创建延迟线程，将失败队列中的请求拿出来，交给线程池处理

    public Runnable delayThread = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    HttpTask take = mDelayQueue.take();
                    if (take.getRetryCount() < 3) {
                        take.setRetryCount(take.getRetryCount() + 1);
                        Log.e("run: ", "重试");
                    } else {
                        Log.e("run: ", "重试超过三次，放弃线程");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
