package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplDoubleEle extends TmplDynamicEle<Double> {

    public TmplDoubleEle(String key, String fmt, String dft) {
        this.key = key;
        this.fmt = Strings.sNull(fmt, "%#.2f");
        this.dft = null == dft ? null : Double.valueOf(dft);
    }

    @Override
    protected String _val(Object val) {
        Double n = null == val ? dft : Castors.me().castTo(val, Double.class);
        if (null != n) {
            return String.format(fmt, n);
        }
        return null;
    }

}
