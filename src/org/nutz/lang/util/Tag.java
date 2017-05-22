package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return this.is("^(HEAD|DIV|P|UL|OL|LI|BLOCKQUOTE|PRE|TITLE|H[1-9]|HR|TABLE|TR|TD)$");
    }

    public boolean isInline() {
        return this.is("^(SPAN|B|I|U|EM|DEL|STRONG|SUB|SUP|CODE|FONT)$");
    }

    public boolean isNoChild() {
        return this.is("^(BR|HR|IMG|LINK|META)$");
    }

    public boolean isHeading() {
        return this.is("^H[1-9]$");
    }

    /**
     * 标题级别
     * 
     * @return 0 表示不是标题， 1-6 分别表示标题级别
     */
    public int getHeadingLevel() {
        if (this.isElement()) {
            Matcher m = Pattern.compile("^H([1-9])$").matcher(tagName());
            if (m.find())
                return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    public boolean isList() {
        return this.is("^[OU]L$");
    }

    public boolean is(String regex) {
        if (this.isTextNode())
            return false;
        String tagName = this.tagName();
        if (regex.startsWith("^"))
            return tagName.matches(regex.toUpperCase());
        return tagName.equals(regex.toUpperCase());
    }

    public boolean isHtml() {
        return this.is("HTML");
    }

    public boolean isBody() {
        return this.is("BODY");
    }

    public boolean isElement() {
        return this.get().isElement();
    }

    public boolean isTextNode() {
        return this.get().isText();
    }

    public boolean isChildAllInline() {
        if (!get().isElement())
            return false;
        for (Node<HtmlToken> ht : this.getChildren())
            if (((Tag) ht).isBlock())
                return false;
        return true;
    }

    public List<Tag> getChildTags() {
        List<Node<HtmlToken>> list = this.getChildren();
        List<Tag> tags = new ArrayList<Tag>(list.size());
        for (Node<HtmlToken> ele : list) {
            tags.add((Tag) ele);
        }
        return tags;
    }

    public String name() {
        return get().getName();
    }

    public String tagName() {
        return get().getTagName();
    }

    public Tag attr(String name, String value) {
        get().attr(name, value);
        return this;
    }

    public Tag attr(String name, int value) {
        return attr(name, String.valueOf(value));
    }

    public String attr(String name) {
        return this.get().getAttrVal(name);
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

    public String getNodeValue() {
        return this.get().getValue();
    }

    public String getText() {
        StringBuilder sb = new StringBuilder();
        for (Node<HtmlToken> nd : this.getChildren()) {
            Tag tag = (Tag) nd;
            // 文本
            if (tag.isTextNode()) {
                sb.append(nd.get().getValue());
            }
            // 空格
            else if (tag.isNoChild()) {
                sb.append(' ');
            }
            // 其他
            else {
                sb.append(tag.getText());
            }
        }
        return sb.toString();
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

            __join_to_string(sb, childTag, level, false);

            if (childTag.isBlock() || childTag.isBody())
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

                if (childTag.isBlock() || childTag.isBody())
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
