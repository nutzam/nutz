package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class String2Enum extends Castor<String, Enum> {

    @SuppressWarnings("unchecked")
    @Override
    public Enum cast(String src, Class<?> toType, String... args) throws FailToCastObjectException {
        return Enum.valueOf((Class<Enum>) toType, src);
    }

}
