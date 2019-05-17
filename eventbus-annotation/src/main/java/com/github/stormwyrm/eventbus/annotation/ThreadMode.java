package com.github.stormwyrm.eventbus.annotation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public enum ThreadMode {
    POSTING,

    MAIN,

    BACKGROUND;

    ThreadMode() {
        LinkedHashMap<TypeElement, List<ExecutableElement>> executableElementsByClass = new LinkedHashMap<>();
        for (Map.Entry<TypeElement, List<ExecutableElement>> me : executableElementsByClass.entrySet()) {
            TypeElement typeElement = me.getKey();
            List<ExecutableElement> executableElements = me.getValue();

        }
    }
}
