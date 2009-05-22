package com.zzh.mvc.access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.Strings;
import com.zzh.mvc.Controllor;
import com.zzh.mvc.Params;
import com.zzh.mvc.Return;

public class Login<T extends Account> implements Controllor {

	public Login(AccountService<T> service) {
		this.service = service;
	}

	private AccountService<T> service;

	private String alias;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Class<T> type = service.getAccountType();
		T acc = type.newInstance();
		Params.getObjectAsNameValuePair(acc, request);
		if (!Strings.isBlank(acc.getName())) {
			T dba = service.verify(acc);
			if (null != dba) {
				Session session = Session.me(request);
				session.setAccount(acc);
				if (null != alias)
					session.setObject(alias, acc);
				return acc;
			}
		}
		return Return.fail("Invalid user");
	}

}
