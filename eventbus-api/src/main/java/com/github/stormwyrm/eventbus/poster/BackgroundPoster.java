package com.github.stormwyrm.eventbus.poster;

import com.github.stormwyrm.eventbus.EventBus;
import com.github.stormwyrm.eventbus.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/22
 * Desc: 后台执行任务的类
 **/
public class BackgroundPoster implements Runnable, Poster {
    private static final String TAG = "BackgroundPoster";
    private final static ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    private PendingPostQueue queue;
    private EventBus eventBus;
    private boolean executorRunning;

    public BackgroundPoster(EventBus eventBus) {
        this.eventBus = eventBus;
        queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                DEFAULT_EXECUTOR_SERVICE.execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                PendingPost pendingPost = queue.poll(1000);
                if (pendingPost == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            executorRunning = false;
                            return;
                        }
                    }
                }
                eventBus.invokeSubscriber(pendingPost);
            }
        } finally {
            executorRunning = false;
        }
    }
}
