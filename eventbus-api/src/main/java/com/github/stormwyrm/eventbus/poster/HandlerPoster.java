package com.github.stormwyrm.eventbus.poster;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.github.stormwyrm.eventbus.EventBus;
import com.github.stormwyrm.eventbus.Subscription;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/23
 * Desc: ui线程来执行任务
 **/
public class HandlerPoster extends Handler implements Poster {
    private PendingPostQueue queue;
    private EventBus eventBus;
    private boolean handlerActive;

    public HandlerPoster(EventBus eventBus) {
        super(Looper.getMainLooper());
        this.eventBus = eventBus;
        queue = new PendingPostQueue();
    }

    @Override
    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!handlerActive) {
                handlerActive = true;
                sendMessage(obtainMessage());
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        try {
            while (true) {
                PendingPost pendingPost = queue.poll();
                if (pendingPost == null) {
                    synchronized (this) {
                        // Check again, this time in synchronized
                        pendingPost = queue.poll();
                        if (pendingPost == null) {
                            handlerActive = false;
                            return;
                        }
                    }
                }
                eventBus.invokeSubscriber(pendingPost);
            }
        } finally {
            handlerActive = false;
        }
    }
}
