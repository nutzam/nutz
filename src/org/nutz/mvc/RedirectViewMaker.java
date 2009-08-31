package org.nutz.mvc;

import org.nutz.ioc.ValueMaker;
import org.nutz.ioc.meta.Val;
import org.nutz.mvc.view.RedirectView;

public class RedirectViewMaker implements ValueMaker {

	public String forType() {
		return Val.redirect;
	}

	public Object make(Val val) {
		return new RedirectView(val.getValue().toString());
	}
}