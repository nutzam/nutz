package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Lang;

public class String2Boolean extends Castor<String, Boolean> {

    @Override
    public Boolean cast(String src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (src.length() == 0)
            return false;
        return Lang.parseBoolean(src);
    }

}
