package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Strings;

class TmplBooleanEle extends TmplDynamicEle {

    private String[] texts;

    public TmplBooleanEle(String key, String fmt, String dft) {
        super("boolean", key, fmt, dft);
        this.texts = Strings.splitIgnoreBlank(Strings.sNull(fmt, "false/true"), "\\/");
    }

    @Override
    protected String _val(Object val) {
        boolean b = false;
        if (null != val) {
            b = Castors.me().castTo(val, Boolean.class);
        }
        return b ? texts[1] : texts[0];
    }

}
