package com.github.stormwyrm.eventbus.data;

import com.github.stormwyrm.eventbus.SubscribeMethod;

import java.util.List;

/**
 * 用于存储类相关的注释信息
 */
public interface SubscribeInfo {
    Class<?> getSubscriberClass();

    List<SubscribeMethod> getSubscribeMethod();

}
