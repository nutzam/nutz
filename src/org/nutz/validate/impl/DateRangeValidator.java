package org.nutz.validate.impl;

import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.lang.util.DateRegion;
import org.nutz.lang.util.Region;
import org.nutz.validate.NutValidateException;
import org.nutz.validate.NutValidator;

public class DateRangeValidator implements NutValidator {

    private DateRegion range;

    public DateRangeValidator(String str) {
        this.range = Region.Date(str);
    }

    @Override
    public Object check(Object val) throws NutValidateException {
        if (null == val)
            return null;
        Date d = Castors.me().castTo(val, Date.class);
        if (!range.match(d)) {
            throw new NutValidateException("DateOutOfRange", range.toString(), d);
        }
        return val;
    }

    @Override
    public int order() {
        return 102;
    }

}
