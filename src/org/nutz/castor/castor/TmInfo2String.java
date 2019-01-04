package org.nutz.castor.castor;

import org.nutz.castor.Castor;
import org.nutz.castor.FailToCastObjectException;
import org.nutz.lang.Times.TmInfo;

public class TmInfo2String extends Castor<TmInfo, String> {

    private String format = "HH:mm:ss";

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String cast(TmInfo src, Class<?> toType, String... args)
            throws FailToCastObjectException {
        if (null != src)
            return src.toString(format);
        return null;
    }

}
