package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutBean;

class TmplStaticEle implements TmplEle {

    private String str;

    public TmplStaticEle(String str) {
        this.str = str;
    }

    @Override
    public void join(StringBuilder sb, NutBean context, boolean showKey) {
        sb.append(str);
    }

    @Override
    public String toString() {
        return str.replace("$", "$$");
    }

}
