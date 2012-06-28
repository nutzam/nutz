package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.nutz.lang.meta.Pair;

public class HtmlToken {

    private static final Pattern BLOCK = Pattern.compile(    "^(head|div|p|ul|ol|blockquote|pre|title|h[1-9]|li|hr|table|tr|td)$",
                                                            Pattern.CASE_INSENSITIVE);

    private static final Pattern INLINE = Pattern.compile(    "^(span|b|i|u|em|strong|sub|sup|code|font)$",
                                                            Pattern.CASE_INSENSITIVE);

    private static final Pattern NOCHILD = Pattern.compile(    "^(br|img|link|hr|meta)$",
                                                            Pattern.CASE_INSENSITIVE);

    HtmlToken() {
        attributes = new ArrayList<Pair<String>>();
    }

    private String name;
    private String value;
    private List<Pair<String>> attributes;

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

    public boolean isBlock() {
        if (null == name)
            return false;
        return BLOCK.matcher(name).find();
    }

    public boolean isInline() {
        if (null == name)
            return false;
        return INLINE.matcher(name).find();
    }

    public boolean isNoChild() {
        if (null == name)
            return true;
        return NOCHILD.matcher(name).find();
    }

    public boolean isHtml() {
        if (null == name)
            return false;
        return name.equalsIgnoreCase("html");
    }

    public boolean isBody() {
        if (null == name)
            return false;
        return name.equalsIgnoreCase("body");
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
