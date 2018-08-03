package org.nutz.lang.tmpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

class TmplStringEle extends TmplDynamicEle {

    private Map<String, String> mapping;

    public TmplStringEle(String key, String fmt, String dft) {
        super(null, key, null, dft);
        this.fmt = Strings.sNull(fmt, null);
        // 表示映射数据
        if (null != this.fmt && this.fmt.startsWith(":")) {
            mapping = new HashMap<String, String>();
            String[] ss = Strings.splitIgnoreBlank(this.fmt.substring(1));
            for (String s : ss) {
                Pair<String> p = Pair.create(s);
                mapping.put(p.getName(), p.getValue());
            }
        }
    }

    @Override
    protected String _val(Object val) {
        if (null != val) {
            if (val.getClass().isArray()) {
                return Lang.concat(", ", (Object[]) val).toString();
            }
            if (val instanceof Collection<?>) {
                return Strings.join(", ", (Collection<?>) val);
            }
        }
        String re = Castors.me().castTo(val, String.class);
        if (null != mapping) {
            return Strings.sNull(mapping.get(re), re);
        }
        if (!Strings.isBlank(this.fmt)) {
            return String.format(fmt, re);
        }
        return re;
    }

}
