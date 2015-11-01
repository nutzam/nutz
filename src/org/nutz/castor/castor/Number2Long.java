package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Number2Long extends Castor<Number, Long> {

    @Override
    public Long cast(Number src, Class<?> toType, String... args) {
        return src.longValue();
    }

}
