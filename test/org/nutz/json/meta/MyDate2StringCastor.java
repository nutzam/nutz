package org.nutz.json.meta;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;

public class MyDate2StringCastor extends Castor<Date, String> {
    
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");

    public String cast(Date src, Class<?> toType, String... args) throws FailToCastObjectException {
        return ((SimpleDateFormat)df.clone()).format(src);
    }

}
