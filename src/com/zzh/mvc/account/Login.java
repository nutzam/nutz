package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.Strings;
import com.zzh.mvc.Controllor;
import com.zzh.mvc.Mvc;
import com.zzh.mvc.Return;
import com.zzh.service.EntityService;

public class Login<T extends Account> implements Controllor {

	private EntityService<T> service;

	public Login(EntityService<T> service) {
		this.service = service;
	}

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Class<T> type = service.getEntity().getMirror().getType();
		Account acc = type.newInstance();
		Mvc.getObjectAsNameValuePair(acc, request);
		if (!Strings.isBlank(acc.getName())) {
			Account dba = service.dao().fetch(type, acc.getName());
			if (null != dba)
				if (Strings.equalsIgnoreCase(acc.getPassword(), dba.getPassword())) {
					Session.me(request).setAccount(acc);
					return acc;
				}
		}
		return Return.fail("Invalid user");
	}

}
