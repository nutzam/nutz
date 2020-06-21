package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class TmplBooleanEle extends TmplDynamicEle {

    private String[] texts;

    public TmplBooleanEle(String key, String fmt, String dft) {
        super("boolean", key, fmt, dft);
        if (Strings.isBlank(fmt)) {
            this.texts = Lang.array("false", "true");
        }
        // 定制了
        else {
            this.texts = Strings.sNull(fmt, "false/true").split("/");
            if (this.texts.length == 1) {
                this.texts = Lang.array("", this.texts[0]);
            }
        }
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
