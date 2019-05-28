package com.github.stormwyrm.eventbus;

import com.github.stormwyrm.eventbus.annotation.Subscribe;
import com.github.stormwyrm.eventbus.annotation.ThreadMode;
import com.github.stormwyrm.eventbus.data.SubscribeInfo;
import com.github.stormwyrm.eventbus.data.SubscribeInfoIndex;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 根据注册的类 寻找对应的SubscribeMethod方法集合
 */
class SubscribeInfoFinder {
    private HashMap<Class<?>, List<SubscribeMethod>> METHOD_CACHE = new HashMap<>(); //缓存读取的方法
    private boolean isSkipGenerateIndex;

    public SubscribeInfoFinder(boolean isSkipGenerateIndex) {
        this.isSkipGenerateIndex = isSkipGenerateIndex;
    }

    public List<SubscribeMethod> findSubscribeInfo(Class<?> subscriberClass) {
        List<SubscribeMethod> subscribeMethods = METHOD_CACHE.get(subscriberClass);
        if (subscribeMethods != null) {
            return subscribeMethods;
        }

        if (isSkipGenerateIndex) {
            subscribeMethods = findSubscribeMethodByReflex(subscriberClass);
        } else {
            subscribeMethods = findSubscribeMethodByIndex(subscriberClass);

        }

        METHOD_CACHE.put(subscriberClass, subscribeMethods);//保存寻找过的类对应的方法

        return subscribeMethods;
    }

    //通过注解编译器生成的类来获取信息
    private List<SubscribeMethod> findSubscribeMethodByIndex(Class<?> subscriberClass) {
        try {
            Class<?> subscribeInfoIndexClass = Class.forName("com.github.stormwyrm.eventbus.DefaultSubscribeInfoIndex");
            SubscribeInfoIndex subscribeInfoIndex = (SubscribeInfoIndex) subscribeInfoIndexClass.newInstance();
            SubscribeInfo subscribeInfo = subscribeInfoIndex.getSubscribeInfo(subscriberClass);
            List<SubscribeMethod> subscribeMethods = new ArrayList<>();
            for (SubscribeMethod subscribeMethod : subscribeInfo.getSubscribeMethod()) {
                subscribeMethods.add(subscribeMethod);
            }
            return subscribeMethods;
        } catch (Exception e) {
            e.printStackTrace();
            return findSubscribeMethodByReflex(subscriberClass);
        }
    }

    //通过反射来获取对应的方法信息
    private List<SubscribeMethod> findSubscribeMethodByReflex(Class<?> subscriberClass) {
        List<SubscribeMethod> subscribeMethods = new ArrayList<>();
        Method[] declaredMethods = subscriberClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
            Subscribe annotation = method.getAnnotation(Subscribe.class);
            if (annotation != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if(parameterTypes.length > 1){
                    continue;
                }
                Class<?> eventType = parameterTypes[0];
                boolean sticky = annotation.isSticky();
                ThreadMode threadMode = annotation.threadMode();
                subscribeMethods.add(new SubscribeMethod(method, eventType, threadMode, sticky));
            }
        }
        return subscribeMethods;
    }
}
