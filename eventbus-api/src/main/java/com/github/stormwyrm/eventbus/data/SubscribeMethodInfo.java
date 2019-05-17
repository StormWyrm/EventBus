package com.github.stormwyrm.eventbus.data;

import com.github.stormwyrm.eventbus.annotation.ThreadMode;

/**
 * 用于subscribe注解方法的相关信息
 */
public class SubscribeMethodInfo {
    private ThreadMode threadMode;
    private boolean isStiky;
    private String methodName;
    private Class<?> eventType;

    public SubscribeMethodInfo(ThreadMode threadMode, boolean isStiky, String methodName, Class<?> eventType) {
        this.threadMode = threadMode;
        this.isStiky = isStiky;
        this.methodName = methodName;
        this.eventType = eventType;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public boolean isStiky() {
        return isStiky;
    }

    public void setStiky(boolean stiky) {
        isStiky = stiky;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?> getEventType() {
        return eventType;
    }

    public void setEventType(Class<?> eventType) {
        this.eventType = eventType;
    }
}
