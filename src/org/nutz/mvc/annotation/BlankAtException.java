package org.nutz.mvc.annotation;

import java.lang.reflect.Method;

public class BlankAtException extends RuntimeException {

    private static final long serialVersionUID = 5234371318203199851L;

    public BlankAtException(Class<?> moduleType, Method method) {
        super(String.format("Can not support blank @At in %s.%s",
                            moduleType.getName(),
                            method.getName()));
    }

}
