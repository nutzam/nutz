package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.Strings;
import com.zzh.mvc.Controllor;
import com.zzh.mvc.Mvc;
import com.zzh.mvc.Return;

public class Login<T extends Account> implements Controllor {
	
	public Login(AccountService<T> service) {
		this.service = service;
	}

	private AccountService<T> service;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Class<T> type = service.getAccountType();
		T acc = type.newInstance();
		Mvc.getObjectAsNameValuePair(acc, request);
		if (!Strings.isBlank(acc.getName())) {
			T dba = service.verify(acc);
			if (null != dba) {
				Session.me(request).setAccount(acc);
				return acc;
			}
		}
		return Return.fail("Invalid user");
	}

}
