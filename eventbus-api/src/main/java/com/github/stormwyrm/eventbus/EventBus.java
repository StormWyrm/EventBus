package com.github.stormwyrm.eventbus;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EventBus {
    private final Map<Class<?>, List<Subscription>> subscriptions = new LinkedHashMap<>();//存储类对应的方法和对象
    private final Map<Class<?>,List<Class<?>>> eventTyps = new LinkedHashMap<>();//存储类对应的参数类型
    private final Map<Class<?>,List<Subscription>> stikySubscription = new LinkedHashMap<>();//用于存储类对应的粘性事件

    private SubscribeInfoFinder subscribeInfoFinder;//查找类相关的注解的方法信息

    public EventBus(){

    }

    public EventBus(EventBusBuilder busBuilder){

    }

    public static EventBus getDefault() {
        return Hodler.instace;
    }

    public void register(Object subscriber) {

    }

    public void unregister(Object subscriber) {

    }

    private static class Hodler {
        private static final EventBus instace = new EventBus();
    }
}
