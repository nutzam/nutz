package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Mirror;

@SuppressWarnings({"rawtypes"})
public class Enum2Number extends Castor<Enum, Number> {

    @Override
    public Number cast(Enum src, Class<?> toType, String... args) throws FailToCastObjectException {
        Mirror<?> mirror = Mirror.me(Integer.class);
        Integer re = src.ordinal();
        if (mirror.canCastToDirectly(toType))
            return re;
        return (Number) Mirror.me(toType).born(re.toString());
    }

}
