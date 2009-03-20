package com.zzh.dom;

import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Element<P extends Node> extends Node<P, Node> {

	public Text createText(String value) {
		Text txt = this.create(Text.class);
		txt.setValue(value);
		return txt;
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		List<Text> texts = this.getDescendants(Text.class);
		for (Iterator<Text> it = texts.iterator(); it.hasNext();)
			sb.append(it.next().getValue());
		return sb.toString();
	}

	public void setText(String txt) {
		this.getChildren().clear();
		this.createText(txt);
	}
}
