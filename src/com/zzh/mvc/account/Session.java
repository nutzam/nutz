package com.zzh.mvc.account;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Session {

	public static Session me(HttpServletRequest request) {
		return new Session(request.getSession());
	}

	public static Session me(HttpSession session) {
		return new Session(session);
	}

	private HttpSession session;

	private Session(HttpSession session) {
		this.session = session;
	}

	@SuppressWarnings("unchecked")
	public <T extends Account> T getAccount(Class<T> type) {
		return (T) session.getAttribute(type.getName());
	}

	public <T extends Account> void setAccount(T account) {
		session.setAttribute(account.getClass().getName(), account);
	}

	public <T extends Account> T removeAccount(Class<T> type) {
		T re = getAccount(type);
		session.removeAttribute(type.getName());
		return re;
	}

	public <T extends Account> boolean hasAccount(Class<T> type) {
		return null != session.getAttribute(type.getName());
	}

	@SuppressWarnings("unchecked")
	public <C> C getObject(Class<C> type) {
		return (C) session.getAttribute(type.getName());
	}

	public void setObject(Object obj) {
		session.setAttribute(obj.getClass().getName(), obj);
	}

}
