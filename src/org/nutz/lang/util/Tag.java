package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

import static java.lang.String.*;

/**
 * 简便的 Tag 实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Tag extends SimpleNode<HtmlToken> {

    public static Tag tag(String name, String... attrs) {
        Tag tag = new Tag();
        tag.set(new HtmlToken().setName(name));
        if (null != attrs)
            for (String attr : attrs) {
                if (null != attr && attr.length() > 1) {
                    char c = attr.charAt(0);
                    switch (c) {
                    case '.':
                        tag.addClass(attr.substring(1));
                        break;
                    case '#':
                        tag.id(attr.substring(1));
                        break;
                    default:
                        Pair<String> p = Pair.create(attr);
                        tag.attr(p.getName(), p.getValue());
                    }
                }
            }
        return tag;
    }

    public static Tag text(String text) {
        Tag tag = new Tag();
        if (null != text) {
            text = text.replace("&", "&amp;");
            text = text.replace("<", "&lt;").replace(">", "&gt;");
        }
        tag.set(new HtmlToken().setValue(text));
        return tag;
    }

    public boolean isBlock() {
        return get().isBlock();
    }

    public boolean isInline() {
        return get().isInline();
    }

    public boolean isNoChild() {
        return get().isNoChild();
    }

    public boolean isHtml() {
        return "html".equalsIgnoreCase(get().getName());
    }

    public boolean isBody() {
        return "body".equalsIgnoreCase(get().getName());
    }

    public boolean isChildAllInline() {
        if (!get().isElement())
            return false;
        for (Node<HtmlToken> ht : this.getChildren())
            if (ht.get().isBlock())
                return false;
        return true;
    }

    public String name() {
        return get().getName();
    }

    public Tag attr(String name, String value) {
        get().attr(name, value);
        return this;
    }

    public Tag attr(String name, int value) {
        return attr(name, String.valueOf(value));
    }

    public Tag addClass(String name) {
        String cns = get().getAttrVal("class");
        String[] nms = Strings.splitIgnoreBlank(cns, " ");
        if (null == nms) {
            get().attr("class", name);
        } else {
            if (!Lang.contains(nms, name)) {
                get().attr("class", cns + " " + name);
            }
        }
        return this;
    }

    public boolean hasClass(String name) {
        String cns = get().getAttrVal("class");
        if (null == cns || cns.length() < name.length())
            return false;
        return (" " + cns + " ").indexOf(" " + name + " ") != -1;
    }

    public Tag add(String tagName, String... attrs) {
        Tag re = Tag.tag(tagName, attrs);
        this.add(re);
        return re;
    }

    public Tag id(String id) {
        get().attr("id", id);
        return this;
    }

    public String id() {
        return get().getAttrVal("id");
    }

    public Tag setText(String text) {
        this.add(Tag.text(text));
        return this;
    }

    public List<Tag> childrenTag() {
        List<Node<HtmlToken>> children = this.getChildren();
        List<Tag> list = new ArrayList<Tag>(children.size());
        for (Node<HtmlToken> nd : children) {
            list.add((Tag) nd);
        }
        return list;
    }

    public String toString() {
        if (get().isText())
            return get().getValue();
        StringBuilder sb = new StringBuilder();
        if (isNoChild()) {
            return format("<%s%s/>", name(), attributes2String());
        } else if (isInline()) {
            sb.append(format("<%s", name())).append(attributes2String()).append('>');
            for (Node<HtmlToken> tag : getChildren())
                sb.append(tag);
            sb.append(format("</%s>", name()));
        } else {
            sb.append(format("<%s", name()));
            sb.append(attributes2String()).append('>');
            for (Node<HtmlToken> tag : getChildren()) {
                if (tag.get().isBlock() || tag.get().isBody())
                    sb.append('\n');
                sb.append(tag.toString());
            }
            if (!this.isChildAllInline())
                sb.append('\n');
            sb.append(format("</%s>", name()));
        }
        return sb.toString();
    }

    private String attributes2String() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String> attr : get().getAttributes())
            sb.append(' ').append(attr.toString());
        return sb.toString();
    }
}
