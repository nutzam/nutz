package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;

class TmplStringEle extends TmplDynamicEle<String> {

    public TmplStringEle(String key, String dft) {
        this.key = key;
        this.dft = dft;
    }

    @Override
    protected String _val(Object val) {
        return null == val ? dft : Castors.me().castTo(val, String.class);
    }

}
