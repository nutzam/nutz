package org.nutz.lang.tmpl;

import java.util.Date;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;

class TmplDateEle extends TmplDynamicEle<Date> {

    public TmplDateEle(String key, String fmt, String dft) {
        super("date", key, fmt, dft);
        this.fmt = Strings.sNull(fmt, "yyyy-MM-dd'T'HH:mm:ss");
        this.dft = Castors.me().castTo(dft, Date.class);
    }

    @Override
    protected String _val(Object val) {
        Date d = null == val ? dft : Castors.me().castTo(val, Date.class);
        if (null != d)
            return Times.format(fmt, d);
        return null;
    }

}
