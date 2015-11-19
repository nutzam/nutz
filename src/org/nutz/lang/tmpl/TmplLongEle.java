package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplLongEle extends TmplDynamicEle<Long> {

    public TmplLongEle(String key, String fmt, String dft) {
        super("long", key, fmt, dft);
        this.fmt = Strings.sNull(fmt, "%d");
        this.dft = null == dft ? null : Long.valueOf(dft);
    }

    @Override
    protected String _val(Object val) {
        Long n = null == val ? dft : Castors.me().castTo(val, Long.class);
        if (null != n) {
            return String.format(fmt, n);
        }
        return null;
    }

}
