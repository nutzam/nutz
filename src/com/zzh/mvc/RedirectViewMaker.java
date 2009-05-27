package com.zzh.mvc;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;
import com.zzh.mvc.view.RedirectView;

public class RedirectViewMaker implements ValueMaker {

	@Override
	public String forType() {
		return Val.redirect;
	}

	@Override
	public Object make(Val val) {
		return new RedirectView(val.getValue().toString());
	}
}