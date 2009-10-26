package org.nutz.lang.util;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.meta.Pair;

import static java.lang.String.*;

/**
 * 简便的 Tag 实现
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Tag extends SimpleNode<HtmlToken> {

	public static Tag tag(String name) {
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

	public static void main(String[] args) {
		Tag tag = tag("html");
		tag.add(tag("head").add(tag("title").add(text("Test web Page"))));
		Tag body = tag("body");
		body.attr("bgcolor", "#FFC").attr("margin", "4");
		body.add(tag("h3").add(text("headhead!!!")));
		body.add(tag("ul").add(tag("li").add(text("A"))).add(tag("li").add(text("B"))));
		body.add(tag("hr"));
		body.add(tag("div").add(tag("b").add(text("I am bold"))));
		tag.add(body);
		System.out.println(tag);
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
		for (Pair attr : get().getAttributes())
			sb.append(' ').append(attr.toString());
		return sb.toString();
	}
}
