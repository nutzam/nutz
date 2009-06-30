package org.nutz.dom;

import java.util.HashMap;
import java.util.Map;

import org.nutz.lang.Strings;

public class Dom {

	public Dom() {
		body = new Body(this);
		idMaps = new HashMap<String, Node<?, ?>>();
	}

	private Body body;
	private Map<String, Node<?, ?>> idMaps;

	public Body body() {
		return body;
	}

	protected Dom register(Node<?, ?> node) {
		if (!Strings.isBlank(node.getId()))
			idMaps.put(node.getId(), node);
		return this;
	}

	protected Dom degister(String id) {
		idMaps.remove(id);
		return this;
	}
	
	public void ready() {
		body.ready();
	}

}
