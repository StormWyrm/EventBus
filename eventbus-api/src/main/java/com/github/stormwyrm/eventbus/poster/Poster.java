package com.github.stormwyrm.eventbus.poster;

import com.github.stormwyrm.eventbus.Subscription;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/22
 * Desc: 处理事件的接口类
 **/
public interface Poster {

    void enqueue(Subscription subscription, Object event);
}
