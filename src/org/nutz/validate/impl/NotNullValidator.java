package org.nutz.validate.impl;

import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class NotNullValidator implements NutValidator {

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null== val) {
            throw new NutValidateException("IsNull");
        }
        return val;
    }

    @Override
    public int order() {
        return 0;
    }

}
