package org.nutz.http.impl.wrapper;

import javax.servlet.http.HttpSession;

import org.nutz.http.impl.NutHttpSession;

public class NutHttpSessionWrapper extends NutHttpSession {

	protected HttpSession session;
	
	public NutHttpSessionWrapper(HttpSession session) {
		this.session = session;
	}
}
