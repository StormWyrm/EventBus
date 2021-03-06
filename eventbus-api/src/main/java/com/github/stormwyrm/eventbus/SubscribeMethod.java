package com.github.stormwyrm.eventbus;

import com.github.stormwyrm.eventbus.annotation.ThreadMode;
import com.github.stormwyrm.eventbus.data.SubscribeMethodInfo;

import java.lang.reflect.Method;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/16
 * Desc: 储存方法和类型
 **/
public class SubscribeMethod {
    private Method method;
    private Class<?> eventType;
    private ThreadMode threadMode;
    private boolean isSticky;

    public SubscribeMethod(Method method, Class<?> eventType, ThreadMode threadMode, boolean isSticky) {
        this.method = method;
        this.eventType = eventType;
        this.threadMode = threadMode;
        this.isSticky = isSticky;
    }

    public SubscribeMethod(Method method, SubscribeMethodInfo subscribeMethodInfo) {
        this.method = method;
        this.eventType = subscribeMethodInfo.getEventType();
        this.threadMode = subscribeMethodInfo.getThreadMode();
        this.isSticky = subscribeMethodInfo.isSticky();
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public boolean isSticky() {
        return isSticky;
    }

    public void setSticky(boolean sticky) {
        isSticky = sticky;
    }

}
