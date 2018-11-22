package org.nutz.castor.castor;

import java.lang.reflect.Array;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;

public class Array2Object extends Castor<Object, Object> {

    public Array2Object() {
        this.fromClass = Array.class;
        this.toClass = Object.class;
    }

    @Override
    public Object cast(Object src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (Array.getLength(src) == 0)
            return null;
        return Castors.me().castTo(Array.get(src, 0), toType);
    }

}
