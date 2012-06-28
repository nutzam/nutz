package org.nutz.castor.castor;

import java.util.Calendar;
import java.util.Date;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Calendar2Datetime extends Castor<java.util.Calendar, java.util.Date> {

    @Override
    public Date cast(Calendar src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        return src.getTime();
    }

}
