package com.github.stormwyrm.eventbus.data;

/**
 * 用于获取类相关的信息的接口
 */
public interface SubscribeInfoIndex {
    SubscribeInfo getSubscribeInfo(Class<?> subscriber);
}
