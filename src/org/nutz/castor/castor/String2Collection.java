package org.nutz.castor.castor;

import java.util.Collection;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.json.Json;
import org.nutz.lang.Lang;

@SuppressWarnings({"rawtypes"})
public class String2Collection extends Castor<String, Collection> {

    @Override
    public Collection cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return (Collection) Json.fromJson(toType, Lang.inr(src));
    }

}
