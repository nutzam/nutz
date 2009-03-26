package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;

public class Logout<T extends Account> implements Controllor {
	
	public Logout(AccountService<T> service) {
		this.service = service;
	}
	
	private AccountService<T> service;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return Session.me(request).removeAccount(service.getAccountType());
	}

}
