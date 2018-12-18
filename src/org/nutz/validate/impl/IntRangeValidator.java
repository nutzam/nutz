package org.nutz.validate.impl;

import org.nutz.castor.Castors;
import org.nutz.lang.util.IntRegion;
import org.nutz.lang.util.Region;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class IntRangeValidator implements NutValidator {

    private IntRegion range;

    public IntRangeValidator(String str) {
        this.range = Region.Int(str);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if(null==val)
            return null;
        int n = Castors.me().castTo(val, Integer.class);
        if (!range.match(n)) {
            throw new NutValidateException("IntOutOfRange", range.toString(), n);
        }
        return val;
    }

    @Override
    public int order() {
        return 101;
    }

}
