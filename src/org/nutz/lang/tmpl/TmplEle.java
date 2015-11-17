package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutBean;

interface TmplEle {

    void join(StringBuilder sb, NutBean context, boolean showKey);

}
