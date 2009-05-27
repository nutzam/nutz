package com.zzh.mvc;

import com.zzh.ioc.ValueMaker;
import com.zzh.ioc.meta.Val;

import com.zzh.mvc.view.JspView;

public class JspViewMaker implements ValueMaker {

	@Override
	public String forType() {
		return Val.jsp;
	}

	@Override
	public Object make(Val val) {
		return new JspView(val.getValue().toString());
	}
}
