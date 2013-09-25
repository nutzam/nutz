package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Number2Double extends Castor<Number, Double> {

    @Override
    public Double cast(Number src, Class<?> toType, String... args) {
        return src.doubleValue();
    }

}
