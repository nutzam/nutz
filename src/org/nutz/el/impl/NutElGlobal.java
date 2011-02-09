package org.nutz.el.impl;

import org.nutz.lang.Strings;

public class NutElGlobal {

	public String trim(CharSequence cs) {
		return Strings.trim(cs);
	}

	public int parseInt(CharSequence cs) {
		return Integer.parseInt(cs.toString());
	}

	public String dup(CharSequence cs, int num) {
		return Strings.dup(cs, num);
	}

}
