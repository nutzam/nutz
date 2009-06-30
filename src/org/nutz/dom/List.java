package org.nutz.dom;

@SuppressWarnings("unchecked")
public class List extends Node<Element, ListItem> {

	public ListItem createItem(String value) {
		ListItem item = create(ListItem.class);
		item.createText(value);
		return item;
	}

}
