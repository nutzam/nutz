package org.nutz.lang.meta;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

/**
 * 简便的名值对实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Pair<T> {

    public static Pair<String> create(String s) {
        String[] ss = Strings.splitIgnoreBlank(s, "=");
        String name = null;
        String value = null;
        String pattern = PTN_3;
        if (null != ss)
            if (ss.length == 1) {
                name = ss[0];
            } else if (ss.length == 2) {
                name = ss[0];
                if (ss[1].length() > 0) {
                    if (ss[1].charAt(0) == '"') {
                        value = ss[1].substring(1, ss[1].length() - 1);
                        pattern = PTN_3;
                    } else if (ss[1].charAt(0) == '\'') {
                        value = ss[1].substring(1, ss[1].length() - 1);
                        pattern = PTN_2;
                    } else {
                        value = ss[1];
                        pattern = PTN_1;
                    }
                }
            }
        Pair<String> re = new Pair<String>(name, value);
        re.pattern = pattern;
        return re;
    }

    public Pair() {}

    private static final String PTN_1 = "%s=%s";
    private static final String PTN_2 = "%s='%s'";
    private static final String PTN_3 = "%s=\"%s\"";

    public Pair(String name, T value) {
        this.name = name;
        this.value = value;
        pattern = PTN_3;
    }

    private String name;

    private T value;

    private String pattern;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getValue() {
        return value;
    }

    public String getValueString() {
        return value == null ? null : value.toString();
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Pair<?>)
            if (Strings.equals(((Pair<?>) obj).name, name))
                return Lang.equals(((Pair<?>) obj).value, value);
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        String v = null == value ? "" : value.toString();
        v = v.replace("\"", "&quot;");
        return String.format(pattern, name, v);
    }

}
