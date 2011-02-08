package org.nutz.el.impl.loader;

import java.util.Arrays;

import org.nutz.el.ElOperator;
import org.nutz.lang.Strings;

class OptNode {

	private char[] chars;

	private OptNode[] children;

	private ElOperator operator;

	public ElOperator getOperator() {
		return operator;
	}

	public void setOperator(ElOperator operator) {
		this.operator = operator;
	}

	/**
	 * 根据给定的字符，创建一个子节点。如果该节点存在，则返回该节点
	 * <p>
	 * 这是一个没有考虑效率的实现，幸运的是，这个函数不需要效率
	 * 
	 * @param c
	 *            字符
	 * @return 节点对象
	 */
	OptNode addNode(char c) {
		// for empty PathNode
		if (null == chars) {
			chars = new char[1];
			chars[0] = c;
			children = new OptNode[1];
			children[0] = new OptNode();
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
		OptNode[] nodes = new OptNode[chars.length + 1];
		int j = 0;
		for (; j < i; j++) {
			newcs[j] = chars[j];
			nodes[j] = children[j];
		}
		newcs[j] = c;
		OptNode re = new OptNode();
		nodes[j] = re;
		for (; j < chars.length; j++) {
			newcs[j + 1] = chars[j];
			nodes[j + 1] = children[j];
		}
		chars = newcs;
		children = nodes;
		return re;
	}

	/**
	 * 根据路径获得对应子节点
	 * 
	 * @param cs
	 *            查找路径
	 * @return 节点对象。null 表示不存在
	 */
	OptNode getChild(char c) {
		if (null == chars)
			return null;
		int index = Arrays.binarySearch(chars, c);
		return index >= 0 ? children[index] : null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("$ROOT:");
		appendTo(sb, 1);
		return sb.toString();
	}

	private void appendTo(StringBuilder sb, int depth) {
		sb.append(operator == null ? "<null>" : "[" + operator + "]");
		if (null != chars)
			for (int i = 0; i < this.chars.length; i++) {
				sb.append(String.format("\n%s'%c': ", Strings.dup("   ", depth), chars[i]));
				children[i].appendTo(sb, depth + 1);
			}
	}

}
