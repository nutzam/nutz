package org.nutz.castor.castor;

import java.sql.Timestamp;

import org.nutz.lang.Times;

public class Timestamp2String extends DateTimeCastor<Timestamp, String> {

    @Override
    public String cast(Timestamp src, Class<?> toType, String... args) {
        return Times.sDTms4(Times.D(src.getTime()));
    }

}
