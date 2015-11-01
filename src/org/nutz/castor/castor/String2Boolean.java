package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class String2Boolean extends Castor<String, Boolean> {

    @Override
    public Boolean cast(String src, Class<?> toType, String... args) {
        if (Strings.isBlank(src))
            return false;
        return Lang.parseBoolean(src);
    }

}
