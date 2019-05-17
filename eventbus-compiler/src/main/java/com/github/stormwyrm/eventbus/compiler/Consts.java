package com.github.stormwyrm.eventbus.compiler;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/17
 * Desc: 保存常量值
 **/
public class Consts {
    public static final String PACKAGE_NAME = "com.github.stormwyrm.eventbus";
    public static final String GENERATE_CLASS_NAME = "DefaultSubscribeInfoIndex";
    public static final String DATA = "data";

    //Custom class
    public static final String SUBSCRIBE_INFO_INDEX = PACKAGE_NAME + "." + DATA + '.' + "SubscribeInfoIndex";
    public static final String SUBSCRIBE_INFO = PACKAGE_NAME + "." + DATA + '.' + "SubscribeInfo";
    public static final String SIMPLE_SUBSCRIBE_INFO = PACKAGE_NAME + "." + DATA + '.' + "SimpleSubscribeInfo";
    public static final String SUBSCRIBE_METHOD_INFO = PACKAGE_NAME + "." + DATA + '.' + "SubscribeMethodInfo";


    //System class
    public static final String LIST = "java.util.List";
    public static final String ARRAY_LIST = "java.util.ArrayList";
    public static final String HASH_MAP = "java.util.HashMap";
    public static final String MAP = "java.util.Map";
    public static final String MAP_ENTRY = "java.util.Map.Entry";

}
