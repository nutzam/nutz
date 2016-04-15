package org.nutz.mvc.impl;

import java.util.HashMap;

import org.nutz.lang.Strings;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;
import org.nutz.lang.tmpl.Tmpl;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.NutBean;

public class NutMessageMap extends HashMap<String, Object> {

    private static final long serialVersionUID = 3910572112957799492L;

    public Object get(Object key) {
        return Strings.sNull(super.get(key), key.toString());
    }

    /**
     * @deprecated
     */
    public String get(String key, Context context) {
        Object obj = super.get(key);
        if (null == obj)
            return key;
        if (obj instanceof Segment)
            return Segments.replace((Segment) obj, context);
        return obj.toString();
    }

    public String get(String key, NutBean context) {
        Object obj = super.get(key);
        if (null == obj)
            return key;
        return Tmpl.exec(obj.toString(), context);
    }

    public Object getObject(String key) {
        return super.get(key);
    }

}
