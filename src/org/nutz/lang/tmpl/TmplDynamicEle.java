package org.nutz.lang.tmpl;

import org.nutz.lang.util.NutBean;
import org.nutz.mapl.Mapl;

abstract class TmplDynamicEle<T> implements TmplEle {

    private String type;

    private String key;

    private String orgFmt;

    private String dft;

    protected String fmt;

    protected TmplDynamicEle(String type, String key, String fmt, String dft_str) {
        this.type = type;
        this.key = key;
        this.orgFmt = fmt;
        this.dft = dft_str;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("${").append(key);
        if (null != type) {
            sb.append('<').append(type);
            if (null != orgFmt) {
                sb.append(':').append(orgFmt);
            }
            sb.append('>');
        }
        if (null != dft) {
            sb.append('?').append(dft);
        }
        return sb.append('}').toString();
    }

    @Override
    public void join(StringBuilder sb, NutBean context, boolean showKey) {
        Object val = Mapl.cell(context, key);
        if (null == val && null != dft) {
            val = Mapl.cell(context, dft);
        }
        if (null == val) {
            val = dft;
        }
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
