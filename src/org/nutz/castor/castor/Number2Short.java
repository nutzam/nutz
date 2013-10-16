package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Number2Short extends Castor<Number, Short> {

    @Override
    public Short cast(Number src, Class<?> toType, String... args) {
        return src.shortValue();
    }

}
