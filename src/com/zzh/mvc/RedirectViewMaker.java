package com.zzh.mvc;

import java.util.Map;

import com.zzh.ioc.ObjectMaker;
import com.zzh.mvc.view.RedirectView;

public class RedirectViewMaker extends ObjectMaker {
	@Override
	protected boolean accept(Map<String, Object> properties) {
		return properties.containsKey("redirect");
	}

	@Override
	protected View make(Map<String, Object> properties) {
		return new RedirectView(properties.get("redirect").toString());
	}
}