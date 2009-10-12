package org.nutz.mvc.init;

import org.nutz.lang.Maths;
import org.nutz.lang.util.LinkedArray;

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
		for (char c : cs) {
			if (c == '*')
				break;
			node = node.push(c);
		}
		node.obj = obj;
	}

	T get(String path) {
		LinkedArray<T> stack = new LinkedArray<T>(3);
		char[] cs = path.toLowerCase().toCharArray();
		PathNode<T> node = this;
		for (char c : cs) {
			if (null != node.obj)
				stack.push(node.obj);
			if (null == node.chars)
				break;
			int index = Maths.find(node.chars, c);
			if (index < 0)
				break;
			node = node.children[index];
		}
		if (null != node.obj)
			return node.obj;
		return stack.last();
	}

}
