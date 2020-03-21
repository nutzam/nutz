package org.nutz.validate.impl;

import org.nutz.castor.Castors;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class BoolValueValidator implements NutValidator {

    private boolean B;

    public BoolValueValidator(Object any) {
        this.B = Castors.me().castTo(any, Boolean.class);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        boolean v = Castors.me().castTo(val, Boolean.class);
        return this.B == v;
    }

    @Override
    public int order() {
        return 1000;
    }

}
