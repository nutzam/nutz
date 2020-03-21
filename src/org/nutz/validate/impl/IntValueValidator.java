package org.nutz.validate.impl;

import org.nutz.castor.Castors;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class IntValueValidator implements NutValidator {

    private int N;

    public IntValueValidator(Object any) {
        this.N = Castors.me().castTo(any, Integer.class);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if (null == val) {
            return null;
        }
        int v = Castors.me().castTo(val, Integer.class);
        return this.N == v;
    }

    @Override
    public int order() {
        return 1000;
    }

}
