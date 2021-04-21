package org.nutz.lang.tmpl;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class TmplBooleanEle extends TmplDynamicEle {

    // [false, true]
    private String[] texts;

    public TmplBooleanEle(String key, String fmt, String dft) {
        super("boolean", key, fmt, dft);
        if (Strings.isBlank(fmt)) {
            this.texts = Lang.array("false", "true");
        }
        // 定制了
        else {
            String s = Strings.sNull(fmt, "false/true");
            int pos = s.indexOf('/');
            // "xxx"
            if (pos < 0) {
                texts = Lang.array("", s.trim());
            }
            // "/xxx"
            else if (pos == 0) {
                texts = Lang.array("", s.substring(pos + 1).trim());
            }
            // "xxx/"
            else if (pos == s.length() - 1) {
                texts = Lang.array(s.substring(0, pos).trim(), "");
            }
            // must by "xxx/xxx"
            else {
                texts = Lang.array(s.substring(0, pos).trim(), s.substring(pos + 1).trim());
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
