package com.github.stormwyrm.eventbus.data;

import android.support.annotation.NonNull;

import com.github.stormwyrm.eventbus.SubscribeMethod;

import java.lang.reflect.Method;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/17
 * Desc:
 **/
public class SimpleSubscribeInfo implements SubscribeInfo {
    private Class<?> subscriberClass;
    private SubscribeMethodInfo[] subscribeMethodInfos;

    public SimpleSubscribeInfo(@NonNull Class<?> subscriberClass, SubscribeMethodInfo[] subscribeMethodInfos) {
        this.subscriberClass = subscriberClass;
        this.subscribeMethodInfos = subscribeMethodInfos;
    }

    @Override
    public Class<?> getSubscriberClass() {
        return subscriberClass;
    }

    @Override
    public SubscribeMethod[] getSubscribeMethod() {
        int length = subscribeMethodInfos.length;
        SubscribeMethod[] subscribeMethods = new SubscribeMethod[length];
        for (int i = 0; i < length; i++) {
            SubscribeMethodInfo subscribeMethodInfo = subscribeMethodInfos[i];
            SubscribeMethod subscribeMethod = createSubscribeMethod(subscribeMethodInfo);
            subscribeMethods[i] = (subscribeMethod);
        }
        return subscribeMethods;
    }

    private SubscribeMethod createSubscribeMethod(SubscribeMethodInfo subscribeMethodInfo) {
        try {
            Method method = subscriberClass.getDeclaredMethod(subscribeMethodInfo.getMethodName(), subscribeMethodInfo.getEventType());
            return new SubscribeMethod(method, subscribeMethodInfo);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
