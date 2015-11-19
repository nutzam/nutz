package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplIntEle extends TmplDynamicEle<Integer> {

    public TmplIntEle(String key, String fmt, String dft) {
        super("int", key, fmt, dft);
        this.fmt = Strings.sNull(fmt, "%d");
        this.dft = null == dft ? null : Integer.valueOf(dft);
    }

    @Override
    protected String _val(Object val) {
        Integer n = null == val ? dft : Castors.me().castTo(val, Integer.class);
        if (null != n) {
            return String.format(fmt, n);
        }
        return null;
    }

}
