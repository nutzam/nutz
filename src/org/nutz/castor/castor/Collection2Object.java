package org.nutz.castor.castor;

import java.util.Collection;

import org.nutz.castor.Castor;
import org.nutz.castor.Castors;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"rawtypes"})
public class Collection2Object extends Castor<Collection, Object> {

    @Override
    public Object cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (src.size() == 0)
            return null;
        return Castors.me().castTo(src.iterator().next(), toType);
    }

}
