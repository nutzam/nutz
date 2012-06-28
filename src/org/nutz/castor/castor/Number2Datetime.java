package org.nutz.castor.castor;

import java.util.Date;

import org.nutz.castor.Castor;

public class Number2Datetime extends Castor<Number, java.util.Date> {

    @Override
    public Date cast(Number src, Class<?> toType, String... args) {
        return new java.util.Date(src.longValue());
    }

}
