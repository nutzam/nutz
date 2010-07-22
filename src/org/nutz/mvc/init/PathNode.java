package org.nutz.mvc.init;

import java.util.Arrays;

import org.nutz.lang.Strings;

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
		PathNode<T> wild = null;
		PathNode<T> node = this;
		int i = 0; // 向下走的步数，一步一个 char
		for (; i < cs.length; i++) {
			// 碰到 * 记录之
			if (node.isStar) {
				wild = node;
			}
			// 继续查找是否有更精确的匹配
			char c = cs[i];
			if (null == node.chars)
				break;
			int index = Arrays.binarySearch(node.chars, c);
			// 没有，退出循环
			if (index < 0)
				break;
			// 递归查找
			node = node.children[index];
		}
		// 走完了全部路径
		if (i == cs.length) {
			return new PathInfo<T>(-1, path, node.obj);
		}
		// 没走完路径，但是曾经碰到过一个 *
		else if (null != wild && wild.isStar)
			return new PathInfo<T>(i, path.substring(i), wild.obj);
		// 没走完路径
		return new PathInfo<T>(0, path, null);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("<PathNode>\n");
		appendChildrenTo(sb, 0);
		return sb.toString();
	}

	public void appendChildrenTo(StringBuilder sb, int depth) {
		if (null != chars)
			for (int i = 0; i < this.chars.length; i++) {
				sb.append(String.format("%s%s'%c':[%s]\n",
										Strings.dup("   ", depth),
										isStar ? "*" : " ",
										chars[i],
										children[i].obj));
				children[i].appendChildrenTo(sb, depth + 1);
			}
	}

}
