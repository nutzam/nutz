package org.nutz.validate.impl;

import org.nutz.lang.Strings;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class TrimValidator implements NutValidator {

    @Override
    public Object check(Object val) throws NutValidateException {
        if (null == val)
            return null;
        return Strings.trim(val.toString());
    }

    @Override
    public int order() {
        return 0;
    }

}
