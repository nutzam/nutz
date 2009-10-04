package org.nutz.mvc2.url;

import org.nutz.lang.util.Node;

class PathFinding {

	private char[] cs;
	private int i;
	private Node<Character> node;

	PathFinding(Node<Character> node, char[] cs) {
		this.node = node;
		this.cs = cs;
	}

	PathFinding find() {
		if (i == 0) {
			
		}
		return this;
	}
	
	boolean next(){
		return false;
	}

}
