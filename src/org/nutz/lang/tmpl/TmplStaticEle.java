package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutMap;

class TmplStaticEle implements TmplEle {

    private String str;

    public TmplStaticEle(String str) {
        this.str = str;
    }

    @Override
    public void join(StringBuilder sb, NutMap context, boolean showKey) {
        sb.append(str);
    }

}
