package com.zzh.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.zzh.lang.Lang;

@SuppressWarnings("unchecked")
public abstract class Node<P extends Node, C extends Node> {

	private Dom dom;

	protected Node() {}

	public <E extends C> E create(Class<E> nodeType) {
		try {
			E node = nodeType.newInstance();
			node.parent = this;
			node.dom = this.dom;
			node.children = new ArrayList();
			node.index = this.children.size();
			this.children.add(node);
			return node;
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	private P parent;

	private List<C> children;

	private int deep = -1;

	private String id;

	private String title;

	private String tagName;

	private int index;

	public int getIndex() {
		return index;
	}

	public String getTagName() {
		if (null == tagName)
			return this.tagName = this.getClass().getSimpleName().toUpperCase();
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setId(String id) {
		if (null != this.id)
			dom.degister(this.id);
		this.id = id;
		dom.register(this);
	}

	public int getDeep() {
		if (deep == -1) {
			deep = 0;
			P p = parent;
			while (p != null) {
				p = (P) p.getParent();
				deep++;
			}
		}
		return deep;
	}

	public Dom getDom() {
		return dom;
	}

	public P getParent() {
		return parent;
	}

	public void setParent(P parent) {
		this.parent = parent;
	}

	public List<C> getChildren() {
		return children;
	}

	public C getFirstChild() {
		if (children.size() == 0)
			return null;
		return children.get(0);
	}
	
	public C getLastChild() {
		if (children.size() == 0)
			return null;
		return children.get(children.size()-1);
	}
	
	public <T extends Node> List<T> getChildren(Class<T> classOfT) {
		List<T> list = new LinkedList<T>();
		for (Iterator<C> it = (Iterator<C>) children.iterator(); it.hasNext();) {
			Node node = it.next();
			if (classOfT.isAssignableFrom(node.getClass()))
				list.add((T) node);
		}
		return list;
	}

	public <T extends Node> List<T> getDescendants(Class<T> classOfT) {
		List<T> list = new LinkedList<T>();
		for (Iterator<C> it = (Iterator<C>) children.iterator(); it.hasNext();) {
			Node node = it.next();
			if (classOfT.isAssignableFrom(node.getClass()))
				list.add((T) node);
			else {
				List<T> subs = node.getDescendants(classOfT);
				list.addAll(subs);
			}
		}
		return list;
	}

	public <T extends Node> List<T> getDescendants() {
		List<T> list = new LinkedList<T>();
		for (Iterator<C> it = (Iterator<C>) children.iterator(); it.hasNext();) {
			Node node = it.next();
			list.add((T) node);
			List<T> subs = node.getDescendants();
			list.addAll(subs);
		}
		return list;
	}

	public void setDom(Dom dom) {
		this.dom = dom;
	}

	public void setChildren(List<C> children) {
		this.children = children;
	}

	public void ready() {
		for (Iterator<C> it = children.iterator(); it.hasNext();)
			it.next().ready();
	}
}
