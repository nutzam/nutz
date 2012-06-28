package org.nutz.castor.castor;

import java.sql.Time;
import java.util.Date;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class Datetime2SqlTime extends Castor<Date, Time> {

    @Override
    public Time cast(Date src, Class<?> toType, String... args) throws FailToCastObjectException {
        return new Time(src.getTime());
    }

}
