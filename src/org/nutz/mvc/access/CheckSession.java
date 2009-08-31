package org.nutz.mvc.access;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Controllor;
import org.nutz.mvc.view.RedirectView;

public class CheckSession<T extends Account> implements Controllor {

	private Class<T> accountType;

	private String redirect;

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!Session.me(request).hasAccount(accountType))
			return new RedirectView(redirect);
		return null;
	}

}
