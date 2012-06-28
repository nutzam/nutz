package org.nutz.http;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

public class Cookie {

    protected Map<String, String> map;

    public Cookie() {
        map = new HashMap<String, String>();
    }

    public Cookie(String s) {
        this();
        parse(s);
    }

    public String get(String name) {
        return map.get(name);
    }

    public Cookie remove(String name) {
        map.remove(name);
        return this;
    }

    public Cookie set(String name, String value) {
        map.put(name, value);
        return this;
    }

    public void parse(String str) {
        String[] ss = Strings.splitIgnoreBlank(str, ";");
        for (String s : ss) {
            Pair<String> p = Pair.create(Strings.trim(s));
            map.put(p.getName(), p.getValueString());
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext();) {
            String key = it.next();
            sb.append(key).append('=').append(map.get(key));
            if (it.hasNext())
                sb.append("; ");
        }
        return sb.toString();
    }

}
