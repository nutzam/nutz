package org.nutz.castor.castor;


public class SqlTime2String extends DateTimeCastor<java.sql.Time, String> {

    @Override
    public String cast(java.sql.Time src, Class<?> toType, String... args) {
        return src.toString();
    }

}
