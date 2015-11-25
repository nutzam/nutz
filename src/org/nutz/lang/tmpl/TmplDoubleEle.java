package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplDoubleEle extends TmplDynamicEle<Double> {

    public TmplDoubleEle(String key, String fmt, String dft) {
        super("double", key, fmt, dft);
        this.fmt = Strings.sNull(fmt, "%#.2f");
    }

    @Override
    protected String _val(Object val) {
        Double n = Castors.me().castTo(val, Double.class);
        if (null != n) {
            return String.format(fmt, n);
        }
        return null;
    }

}
