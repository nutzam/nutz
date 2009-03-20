package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;

public class Logout implements Controllor {

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return Session.me(request).removeAccount();
	}

}
