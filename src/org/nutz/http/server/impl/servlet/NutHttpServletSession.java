package org.nutz.http.server.impl.servlet;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.nutz.http.impl.NutHttpSession;
import org.nutz.lang.Lang;

@SuppressWarnings("deprecation")
public class NutHttpServletSession extends NutHttpSession implements HttpSession{

	//------------------------------------------------------------------------------
	
	public HttpSessionContext getSessionContext() {
		throw Lang.noImplement();
	}

	public Object getValue(String arg0) {
		throw Lang.noImplement();
	}

	public String[] getValueNames() {
		throw Lang.noImplement();
	}

	public void putValue(String key, Object value) {
		throw Lang.noImplement();
	}

	public void removeValue(String value) {
		throw Lang.noImplement();
	}
}
