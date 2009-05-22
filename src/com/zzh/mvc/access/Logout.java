package com.zzh.mvc.access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Controllor;
import com.zzh.mvc.Return;

public class Logout<T extends Account> implements Controllor {

	private Class<T> accountType;

	public Logout(Class<T> accountType) {
		this.accountType = accountType;
	}

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (null != request.getSession().getAttribute(Session.class.getName())) {
			Session session = Session.me(request);
			session.removeAccount(accountType);
			session.detach();
		}
		return Return.OK();
	}

}
