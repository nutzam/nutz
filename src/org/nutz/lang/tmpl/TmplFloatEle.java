package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplFloatEle extends TmplDynamicEle<Float> {

    public TmplFloatEle(String key, String fmt, String dft) {
        this.key = key;
        this.fmt = Strings.sNull(fmt, "%#.2f");
        this.dft = null == dft ? null : Float.valueOf(dft);
    }

    @Override
    protected String _val(Object val) {
        Float n = null == val ? dft : Castors.me().castTo(val, Float.class);
        if (null != n) {
            return String.format(fmt, n);
        }
        return null;
    }

}
