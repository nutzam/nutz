package org.nutz.mvc.init;

import java.util.Arrays;

/**
 * 所有的路径将被转换成小写。
 * 
 * @author zozoh
 * 
 */
class PathNode<T> {

	PathNode() {}

	private char[] chars;
	private PathNode<T>[] children;
	private T obj;
	private boolean isStar;

	/**
	 * 这是一个效率很低的实现，幸运的是，这个函数并不需要效率
	 * 
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private PathNode<T> push(char c) {
		// for empty PathNode
		if (null == chars) {
			chars = new char[1];
			chars[0] = c;
			children = new PathNode[1];
			children[0] = new PathNode<T>();
			return children[0];
		}
		// find the insert position
		int i = 0;
		for (; i < chars.length; i++) {
			if (chars[i] == c)
				return children[i];
			if (chars[i] > c)
				break;
		}
		// add new char
		char[] newcs = new char[chars.length + 1];
		PathNode<T>[] nodes = new PathNode[chars.length + 1];
		int j = 0;
		for (; j < i; j++) {
			newcs[j] = chars[j];
			nodes[j] = children[j];
		}
		newcs[j] = c;
		PathNode<T> re = new PathNode<T>();
		nodes[j] = re;
		for (; j < chars.length; j++) {
			newcs[j + 1] = chars[j];
			nodes[j + 1] = children[j];
		}
		chars = newcs;
		children = nodes;
		return re;
	}

	void add(String path, T obj) {
		char[] cs = path.toLowerCase().toCharArray();
		PathNode<T> node = this;
		int i = 0;
		for (; i < cs.length; i++) {
			char c = cs[i];
			if (c == '*') {
				node.isStar = true;
				node.obj = obj;
				break;
			}
			node = node.push(c);
		}
		if (i == cs.length)
			node.obj = obj;
	}

	PathInfo<T> get(String path) {
		char[] cs = path.toLowerCase().toCharArray();
		PathNode<T> node = this;
		int i = 0;
		for (; i < cs.length; i++) {
			if (node.isStar)
				break;
			char c = cs[i];
			if (null == node.chars)
				break;
			int index = Arrays.binarySearch(node.chars, c);
			if (index < 0)
				break;
			node = node.children[index];
		}
		if (i == cs.length)
			return new PathInfo<T>(i, null, node.obj);
		else if (node.isStar)
			return new PathInfo<T>(i, path.substring(i), node.obj);
		return new PathInfo<T>(0, path, null);
	}

}
