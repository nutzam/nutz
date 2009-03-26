package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;
import com.zzh.mvc.View;

public class CheckSession<T extends Account> implements Controllor {

	public CheckSession(AccountService<T> service, View view) {
		this.service = service;
		this.view = view;
	}

	private AccountService<T> service;

	private View view;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (!Session.me(request).hasAccount(service.getAccountType()))
			return view;
		return null;
	}

}
