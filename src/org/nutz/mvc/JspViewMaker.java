package org.nutz.mvc;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;

import org.nutz.mvc.view.JspView;

public class JspViewMaker implements ValueMaker {

	public String forType() {
		return Val.jsp;
	}

	public Object make(Val val) {
		return new JspView(val.getValue().toString());
	}
}
