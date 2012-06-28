package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class Enum2String extends Castor<Enum, String> {

    @Override
    public String cast(Enum src, Class<?> toType, String... args) throws FailToCastObjectException {
        return src.name();
    }
}
