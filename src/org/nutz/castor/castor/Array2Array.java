package org.nutz.castor.castor;

import java.lang.reflect.Array;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

public class Array2Array extends Castor<Object, Object> {

    public Array2Array() {
        this.fromClass = Array.class;
        this.toClass = Array.class;
    }

    @Override
    public Object cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return Lang.array2array(src, toType.getComponentType());
    }

}
