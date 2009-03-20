package com.zzh.dom;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class Body extends Node<Node, Node> {

	protected Body(Dom dom) {
		setDom(dom);
		setChildren(new ArrayList());
	}


}
