package org.nutz.validate.impl;

import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class MaxLengthValidator implements NutValidator {
    
    private int len;

    public MaxLengthValidator(int len) {
        this.len = len;
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null==val)
            return null;
        String s = val.toString();
        if(s.length() > len) {
            throw new NutValidateException("StrTooLong", len, s);
        }
        return val;
    }

    @Override
    public int order() {
        return 12;
    }

}
