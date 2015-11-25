package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;

class TmplStringEle extends TmplDynamicEle<String> {

    public TmplStringEle(String key, String dft) {
        super(null, key, null, dft);
    }

    @Override
    protected String _val(Object val) {
        return Castors.me().castTo(val, String.class);
    }

}
