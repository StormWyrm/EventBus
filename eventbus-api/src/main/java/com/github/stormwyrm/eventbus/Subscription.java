package com.github.stormwyrm.eventbus;

import java.util.Objects;

/**
 * 存储对象以及注解方法
 */
public class Subscription {
    private Object subscriber;
    private SubscribeMethod subscribeMethod;

    public Subscription(Object subscriber, SubscribeMethod subscribeMethod) {
        this.subscriber = subscriber;
        this.subscribeMethod = subscribeMethod;
    }

    public Object getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Object subscriber) {
        this.subscriber = subscriber;
    }

    public SubscribeMethod getSubscribeMethod() {
        return subscribeMethod;
    }

    public void setSubscribeMethod(SubscribeMethod subscribeMethod) {
        this.subscribeMethod = subscribeMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(getSubscriber(), that.getSubscriber()) &&
                Objects.equals(subscribeMethod, that.subscribeMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubscriber(), subscribeMethod);
    }
}
