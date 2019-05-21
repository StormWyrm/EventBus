package com.github.stormwyrm.eventbus;

import com.github.stormwyrm.eventbus.annotation.ThreadMode;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private static final EventBusBuilder DEFAULT_EVENT_BUS_BUILDER = new EventBusBuilder();

    private final ConcurrentHashMap<Class<?>, List<Subscription>> subscriptionsByEventType = new ConcurrentHashMap<>();//存储EventType对应的方法和对象
    private final ConcurrentHashMap<Object, List<Class<?>>> eventTypeBySubscriber = new ConcurrentHashMap<>();//存储类对应的参数类型
    private final ConcurrentHashMap<Class<?>, List<Subscription>> stikySubscription = new ConcurrentHashMap<>();//用于存储类对应的粘性事件

    private SubscribeInfoFinder subscribeInfoFinder;//查找类相关的注解的方法信息

    public EventBus() {
        this(DEFAULT_EVENT_BUS_BUILDER);
    }

    public EventBus(EventBusBuilder busBuilder) {
        subscribeInfoFinder = new SubscribeInfoFinder(busBuilder.isSkipGenerateIndex);
    }

    public static EventBus getDefault() {
        return Hodler.instace;
    }

    public void register(Object subscriber) {
        Class<?> subscriberClass = subscriber.getClass();
        List<SubscribeMethod> subscribeMethods = subscribeInfoFinder.findSubscribeInfo(subscriberClass);
        for (SubscribeMethod subscribeMethod : subscribeMethods) {
            subscribe(subscriber, subscribeMethod);
        }
    }

    private synchronized void subscribe(Object subscriber, SubscribeMethod subscribeMethod) {
        Class<?> eventType = subscribeMethod.getEventType();
        List<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions == null) {
            subscriptions = new ArrayList<>();
            subscriptionsByEventType.put(eventType, subscriptions);
        }
        subscriptions.add(new Subscription(subscriber, subscribeMethod));

        List<Class<?>> eventTypes = eventTypeBySubscriber.get(subscriber);
        if (eventTypes == null) {
            eventTypes = new ArrayList<>();
            eventTypeBySubscriber.put(subscriber, eventTypes);
        }
        eventTypes.add(eventType);
    }

    public void unregister(Object subscriber) {
        List<Class<?>> eventTypes = eventTypeBySubscriber.get(subscriber);
        if (eventTypes != null) {
            for (Class<?> eventType : eventTypes) {
                unsubscribeByEventType(subscriber, eventType);
            }
            eventTypeBySubscriber.remove(subscriber);
        }
    }

    private void unsubscribeByEventType(Object subscriber, Class<?> eventType) {
        List<Subscription> subscriptions = subscriptionsByEventType.get(eventType);
        if (subscriptions != null) {
            int size = subscriptions.size();
            for (int i = 0; i < size; i++) {
                Subscription subscription = subscriptions.get(i);
                if (subscription.getSubscriber() == subscriber) {
                    subscriptions.remove(i);
                    i--;
                }
            }
        }
    }

    public synchronized void postEvent(Object event) {
        List<Subscription> subscriptions = subscriptionsByEventType.get(event.getClass());
        if (subscriptions != null && subscriptions.size() != 0) {
            for (Subscription subscription : subscriptions) {
                postToSubscription(subscription, event, false);
            }
        }
    }

    private void postToSubscription(Subscription subscription, Object event, boolean isMainThread) {
        ThreadMode threadMode = subscription.getSubscribeMethod().getThreadMode();
        switch (threadMode) {
            case POSTING:
                invokeSubscribeMethod(subscription, event);
                break;
            case MAIN:
                invokeSubscribeMethod(subscription, event);
                break;
            case BACKGROUND:
                invokeSubscribeMethod(subscription, event);
                break;
        }
    }

    private void invokeSubscribeMethod(Subscription subscription, Object event) {
        Object subscriber = subscription.getSubscriber();
        try {
            subscription.getSubscribeMethod().getMethod().invoke(subscriber, event);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static class Hodler {
        private static final EventBus instace = new EventBus();
    }
}
