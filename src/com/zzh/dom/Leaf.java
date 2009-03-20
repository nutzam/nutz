package com.zzh.dom;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public abstract class Leaf extends Node<Node, Node> {

	private static final List empty = new ArrayList();

	@Override
	public <E extends Node> E create(Class<E> nodeType) {
		throw new RuntimeException("Img can NOT create child node!");
	}

	@Override
	public List<Node> getChildren() {
		return empty;
	}

	@Override
	public <T extends Node> List<T> getChildren(Class<T> classOfT) {
		return empty;
	}

}
