package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.meta.Pair;

public class HtmlToken {

    HtmlToken() {
        attributes = new ArrayList<Pair<String>>();
    }

    private String name;
    private String value;
    private List<Pair<String>> attributes;

    public String getTagName() {
        if (null == name)
            return null;
        return name.toUpperCase();
    }

    public String getName() {
        return name;
    }

    public HtmlToken setName(String name) {
        this.name = name;
        return this;
    }

    public String getValue() {
        return value;
    }

    public HtmlToken setValue(String value) {
        this.value = value;
        return this;
    }

    public boolean isElement() {
        return null != name;
    }

    public boolean isText() {
        return null == name && value != null;
    }

    public HtmlToken attr(String name, String value) {
        Pair<String> attr = getAttr(name);
        if (null == attr) {
            attr = new Pair<String>(name, value);
            attributes.add(attr);
        } else {
            attr.setValue(value);
        }
        return this;
    }

    public HtmlToken attr(String name, int value) {
        return attr(name, String.valueOf(value));
    }

    public Pair<String> getAttr(String name) {
        for (Pair<String> attr : attributes)
            if (attr.getName().equals(name))
                return attr;
        return null;
    }

    public String getAttrVal(String name) {
        Pair<String> p = getAttr(name);
        return p == null ? null : p.getValueString();
    }

    public List<Pair<String>> getAttributes() {
        return attributes;
    }

}
