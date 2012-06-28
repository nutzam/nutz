package org.nutz.castor.castor;

import java.util.Map;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class Object2Map extends Castor<Object, Map> {

    @SuppressWarnings("unchecked")
    @Override
    public Map cast(Object src, Class<?> toType, String... args) throws FailToCastObjectException {
        return Lang.obj2map(src, (Class<? extends Map>) ((Class<? extends Map>) toType));
    }

}
