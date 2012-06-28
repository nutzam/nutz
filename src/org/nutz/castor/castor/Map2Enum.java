package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings("rawtypes")
public class Map2Enum extends Castor<Map, Enum> {

    @Override
    public Enum cast(Map src, Class<?> toType, String... args) throws FailToCastObjectException {
        return null;
    }

}
