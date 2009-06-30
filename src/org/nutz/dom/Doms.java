package org.nutz.dom;

import java.util.Iterator;

import org.nutz.lang.Strings;

@SuppressWarnings("unchecked")
public class Doms {

	public static void main(String[] args) {
		Dom dom = new Dom();
		Img img = dom.body().create(Img.class);
		img.setSrc("/img/log.png");
		Table table = dom.body().create(Table.class);
		Row row = table.createRow();
		row.createCell("A1");
		row.createCell("A2");
		row = table.createRow();
		Cell cell = row.createCell("B1");
		List list = cell.create(List.class);
		list.createItem("L1");
		list.createItem("L2");
		row.createCell("B2");
		dom.body().create(Text.class).setValue("Section One");
		Anchor a = dom.body().create(Anchor.class);
		a.setHref("http://www.google.com");
		Box box = a.create(Box.class);
		box = box.create(Box.class);
		box.setTagName("B");
		box.createText("Link to google");
		dom.ready();
		System.out.println(dump(dom));
	}

	private static final String tab = "   ";

	public static String dump(Dom dom) {
		StringBuilder sb = new StringBuilder();
		Body body = dom.body();
		sb.append(dump(body));
		return sb.toString();
	}

	private static String dump(Node node) {
		StringBuilder sb = new StringBuilder();
		printNodeSelf(sb, node);
		dumpChildren(node, sb);
		return sb.toString();
	}

	private static void printNodeSelf(StringBuilder sb, Node node) {
		sb.append("\r\n").append(Strings.dup(tab, node.getDeep()));
		if (node.getChildren() != null && node.getChildren().size() > 0)
			sb.append("+ ");
		else
			sb.append("- ");
		sb.append(node.getTagName());
	}

	private static void dumpChildren(Node node, StringBuilder sb) {
		for (Iterator it = node.getChildren().iterator(); it.hasNext();) {
			Node e = (Node) it.next();
			if (e instanceof Text) {
				printNodeSelf(sb, e);
				sb.append(" :\"").append(((Text) e).getValue()).append('"');
			} else if (e instanceof Anchor) {
				printNodeSelf(sb, e);
				sb.append(" : ").append(((Anchor) e).getHref());
				dumpChildren(e, sb);
			} else if (e instanceof Img) {
				printNodeSelf(sb, e);
				sb.append(" : ").append(((Img) e).getSrc());
			} else {
				sb.append(dump(e));
			}
		}
	}
}
