package com.github.stormwyrm.eventbus.compiler;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * Autor: LiQingfeng
 * Date: 2019/5/16
 * Desc: 打印编译时日志工具
 **/
public class Logger {
    private Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
    }

    public void info(String info) {
        messager.printMessage(Diagnostic.Kind.NOTE, "EventBus info -->" + info);
    }

    public void info(String info, Element element) {
        messager.printMessage(Diagnostic.Kind.NOTE, "EventBus info -->" + info, element);
    }

    public void error(String error) {
        messager.printMessage(Diagnostic.Kind.ERROR, "EventBus error -->" + error);
    }

    public void error(String error, Element element) {
        messager.printMessage(Diagnostic.Kind.ERROR, "EventBus error -->" + error, element);
    }
}
