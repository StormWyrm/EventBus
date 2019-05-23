package com.github.stormwyrm.eventbus.poster;

import com.github.stormwyrm.eventbus.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/22
 * Desc:
 **/
public class PendingPost {
    private static final List<PendingPost> PENDING_POST_POOL = new ArrayList<>();

    public Subscription subscription;
    public Object event;
    public PendingPost next;

    public PendingPost(Subscription subscription, Object event) {
        this.subscription = subscription;
        this.event = event;
    }

    public static List<PendingPost> getPendingPostPool() {
        return PENDING_POST_POOL;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public Object getEvent() {
        return event;
    }

    public void setEvent(Object event) {
        this.event = event;
    }

    public PendingPost getNext() {
        return next;
    }

    public void setNext(PendingPost next) {
        this.next = next;
    }

    public static PendingPost obtainPendingPost(Subscription subscription, Object event) {
        synchronized (PENDING_POST_POOL) {
            int size = PENDING_POST_POOL.size();
            if (size > 0) {
                PendingPost pendingPost = PENDING_POST_POOL.remove(size - 1);
                pendingPost.subscription = subscription;
                pendingPost.event = event;
                pendingPost.next = null;
                return pendingPost;
            }
        }
        return new PendingPost(subscription, event);
    }

    public static void releasePendingPost(PendingPost pendingPost) {
        pendingPost.event = null;
        pendingPost.subscription = null;
        pendingPost.next = null;
        synchronized (PENDING_POST_POOL) {
            // Don't let the pool grow indefinitely
            if (PENDING_POST_POOL.size() < 10000) {
                PENDING_POST_POOL.add(pendingPost);
            }
        }
    }

}
