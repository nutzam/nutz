package com.zzh.dom;

import com.zzh.lang.Lang;

public class Rule {

	public Rule(Style style, Selector... selectors) {
		if (null != selectors)
			Lang.makeThrow("Rule must need at least one selector!");
		this.selectors = selectors;
		this.style = style;
	}

	private Selector[] selectors;
	private Style style;

	public Style getStyle() {
		return style;
	}

	public boolean match(Node<?, ?> node) {
		for (Selector slt : selectors)
			if (slt.match(node))
				return true;
		return false;
	}

}
