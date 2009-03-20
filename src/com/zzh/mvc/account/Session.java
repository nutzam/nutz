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

	private String attrName = "account";

	private Session(HttpSession session) {
		this.session = session;
	}

	public Account getAccount() {
		return (Account) session.getAttribute(attrName);
	}

	public void setAccount(Account account) {
		session.setAttribute(attrName, account);
	}

	public Account removeAccount() {
		Account re = getAccount();
		session.removeAttribute(attrName);
		return re;
	}

	public boolean hasAccount() {
		return null != session.getAttribute(attrName);
	}

}
