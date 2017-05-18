package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;

/**
 * 简便的 Tag 实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Tag extends SimpleNode<HtmlToken> {

    public static Tag tag(String name, String... attrs) {
        return NEW(name).attrs(attrs);
    }

    public static Tag NEW(String name) {
        Tag tag = new Tag();
        tag.set(new HtmlToken().setName(name));
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

    public Tag attrs(String... attrs) {
        if (null != attrs) {
            for (String attr : attrs) {
                if (null != attr && attr.length() > 1) {
                    char c = attr.charAt(0);
                    switch (c) {
                    case '.':
                        this.addClass(attr.substring(1));
                        break;
                    case '#':
                        this.id(attr.substring(1));
                        break;
                    default:
                        Pair<String> p = Pair.create(attr);
                        this.attr(p.getName(), p.getValue());
                    }
                }
            }
        }
        return this;
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
        return toString(0);
    }

    public String toString(int level) {
        StringBuilder sb = new StringBuilder();
        __join_to_string(sb, this, level, true);
        return sb.toString();
    }

    public String toOuterHtml(boolean autoIndent) {
        int level = autoIndent ? 0 : -1;
        StringBuilder sb = new StringBuilder();
        __join_to_string(sb, this, level, false);
        return sb.toString();
    }

    public String toInnerHtml(boolean autoIndent) {
        int level = autoIndent ? 0 : -1;
        StringBuilder sb = new StringBuilder();
        
        for (Node<HtmlToken> child : this.getChildren()) {
            Tag childTag = (Tag) child;
            HtmlToken token = childTag.get();

            __join_to_string(sb, childTag, level, false);

            if (token.isBlock() || token.isBody())
                sb.append('\n');
        }
        return sb.toString();
    }

    private static void __join_to_string(StringBuilder sb,
                                         Tag tag,
                                         int level,
                                         boolean closeNoChild) {
        // 纯文本
        if (tag.get().isText()) {
            sb.append(tag.get().getValue());
            return;
        }

        // 统一的缩进前缀
        String prefix = level >= 0 ? Strings.dup(' ', level * 4) : null;

        // 无子节点的标签
        if (tag.isNoChild()) {
            __join_tag_prefix(sb, tag, prefix);
            sb.append('<').append(tag.name());
            __join_attributes(sb, tag);
            if (closeNoChild)
                sb.append('/');
            sb.append('>');
        }
        // 行内元素
        else if (tag.isInline()) {
            __join_tag_prefix(sb, tag, prefix);
            __join_tag_begin(sb, tag);
            for (Node<HtmlToken> child : tag.getChildren()) {
                __join_to_string(sb, (Tag) child, level, closeNoChild);
            }
            __join_tag_end(sb, tag);
        }
        // 那么就是块元素咯
        else {
            __join_tag_prefix(sb, tag, prefix);
            __join_tag_begin(sb, tag);

            for (Node<HtmlToken> child : tag.getChildren()) {
                Tag childTag = (Tag) child;
                HtmlToken token = child.get();

                if (token.isBlock() || token.isBody())
                    sb.append('\n');

                __join_to_string(sb, childTag, level >= 0 ? level + 1 : level, closeNoChild);
            }
            sb.append('\n');
            __join_tag_prefix(sb, tag, prefix);
            __join_tag_end(sb, tag);
        }
    }

    private static void __join_tag_prefix(StringBuilder sb, Tag tag, String prefix) {
        if (null != prefix && prefix.length() > 0)
            sb.append(prefix);
    }

    private static void __join_tag_begin(StringBuilder sb, Tag tag) {
        sb.append('<').append(tag.name());
        __join_attributes(sb, tag);
        sb.append('>');
    }

    private static void __join_tag_end(StringBuilder sb, Tag tag) {
        sb.append("</").append(tag.name()).append('>');
    }

    private static void __join_attributes(StringBuilder sb, Tag tag) {
        for (Pair<String> attr : tag.get().getAttributes())
            sb.append(' ').append(attr.toString());
    }

}
