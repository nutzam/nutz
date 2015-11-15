package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutMap;

interface TmplEle {

    void join(StringBuilder sb, NutMap context, boolean showKey);

}
