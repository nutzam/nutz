package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Boolean2Boolean extends Castor<Boolean, Boolean> {

    @Override
    public Boolean cast(Boolean src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return src;
    }

}
