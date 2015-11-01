package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Number2Char extends Castor<Number, Character> {

    @Override
    public Character cast(Number src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return (char) src.intValue();
    }

}
