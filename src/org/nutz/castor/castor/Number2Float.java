package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Number2Float extends Castor<Number, Float> {

    @Override
    public Float cast(Number src, Class<?> toType, String... args) {
        return src.floatValue();
    }

}
