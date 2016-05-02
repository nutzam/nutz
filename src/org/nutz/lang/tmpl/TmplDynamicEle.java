package org.nutz.lang.tmpl;

import org.nutz.lang.Strings;
import org.nutz.lang.util.NutBean;
import org.nutz.mapl.Mapl;

abstract class TmplDynamicEle<T> implements TmplEle {

    private String _type;

    private String key;

    private String _org_fmt;

    private String _dft_val;

    private String _dft_key;

    protected String fmt;

    protected TmplDynamicEle(String type, String key, String fmt, String dft_str) {
        this._type = type;
        this.key = key;
        this._org_fmt = fmt;

        // 默认值取 key @xxx
        if (!Strings.isBlank(dft_str) && dft_str.startsWith("@")) {
            this._dft_key = dft_str.substring(1);
        }
        // 默认值是静态的
        else {
            this._dft_val = dft_str;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("${").append(key);
        if (null != _type) {
            sb.append('<').append(_type);
            if (null != _org_fmt) {
                sb.append(':').append(_org_fmt);
            }
            sb.append('>');
        }
        // 默认键
        if (null != _dft_key) {
            sb.append('?').append('@').append(_dft_val);
        }
        // 默认值
        else if (null != _dft_val) {
            sb.append('?').append(_dft_val);
        }
        return sb.append('}').toString();
    }

    public void join(StringBuilder sb, NutBean context, boolean showKey) {
        Object val = Mapl.cell(context, key);

        if (null == val) {
            // 默认键
            if (null != _dft_key) {
                val = Mapl.cell(context, _dft_key);
            }
            // 默认值
            else if (null != _dft_val) {
                val = _dft_val;
            }
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
