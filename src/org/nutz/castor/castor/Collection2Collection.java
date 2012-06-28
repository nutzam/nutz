package org.nutz.castor.castor;

import java.util.Collection;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

@SuppressWarnings({"unchecked", "rawtypes"})
public class Collection2Collection extends Castor<Collection, Collection> {

    @Override
    public Collection cast(Collection src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        Collection coll = createCollection(src, toType);
        coll.addAll(src);
        return coll;
    }

}
