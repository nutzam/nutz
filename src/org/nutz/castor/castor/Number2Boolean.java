package org.nutz.castor.castor;

import org.nutz.castor.Castor;

public class Number2Boolean extends Castor<Number, Boolean> {

    @Override
    public Boolean cast(Number src, Class<?> toType, String... args) {
        return src.toString().charAt(0) == '0' ? false : true;
    }

}
