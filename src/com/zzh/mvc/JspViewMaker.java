package com.zzh.mvc;

import java.util.Map;

import com.zzh.ioc.ObjectMaker;
import com.zzh.mvc.view.JspView;

public class JspViewMaker extends ObjectMaker {
	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("jsp");
	}

	@Override
	protected View make(Map<String, Object> properties) {
		return new JspView(properties.get("jsp").toString());
	}
}
