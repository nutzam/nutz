package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;
import com.zzh.mvc.View;

public class CheckSession implements Controllor {

	private View view;

	public CheckSession(View view) {
		this.view = view;
	}

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (!Session.me(request).hasAccount())
			return view;
		return null;
	}

}
