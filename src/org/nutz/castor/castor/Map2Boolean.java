package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;

@SuppressWarnings("rawtypes")
public class Map2Boolean extends Castor<Map, Boolean> {

    @Override
    public Boolean cast(Map src, Class<?> toType, String... args) {
        if (null == src)
            return Boolean.FALSE;

        return true;
    }

}