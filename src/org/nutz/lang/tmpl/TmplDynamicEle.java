package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutBean;
import org.nutz.mapl.Mapl;

abstract class TmplDynamicEle<T> implements TmplEle {

    protected String key;

    protected String fmt;

    protected T dft;

    @Override
    public void join(StringBuilder sb, NutBean context, boolean showKey) {
        Object val = Mapl.cell(context, key);
        String str = _val(val);

        // 如果木值
        if (null == str) {
            if (showKey) {
                sb.append("${").append(key).append('}');
            }
        }
        // 否则填充
        else {
            sb.append(str);
        }
    }

    protected abstract String _val(Object val);

}
