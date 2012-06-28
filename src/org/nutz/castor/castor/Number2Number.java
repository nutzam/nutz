package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class Number2Number extends Castor<Number, Number> {

    @Override
    public Number cast(Number src, Class<?> toType, String... args) {
        try {
            return (Number) Mirror    .me(toType)
                                    .getWrapperClass()
                                    .getConstructor(String.class)
                                    .newInstance(src.toString());
        }
        catch (Exception e) {
            throw Lang.wrapThrow(e);
        }
    }

}
