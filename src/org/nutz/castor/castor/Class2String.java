package org.nutz.castor.castor;

import org.nutz.castor.Castor;

@SuppressWarnings({"rawtypes"})
public class Class2String extends Castor<Class, String> {

    @Override
    public String cast(Class src, Class<?> toType, String... args) {
        return src.getName();
    }

}
