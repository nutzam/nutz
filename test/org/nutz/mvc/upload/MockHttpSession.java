package org.nutz.mvc.upload;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings( { "unchecked", "deprecation" })
public class MockHttpSession implements HttpSession {

	public Object getAttribute(String arg0) {

		return null;
	}

	public Enumeration getAttributeNames() {

		return null;
	}

	public long getCreationTime() {

		return 0;
	}

	public String getId() {

		return null;
	}

	public long getLastAccessedTime() {

		return 0;
	}

	public int getMaxInactiveInterval() {

		return 0;
	}

	public ServletContext getServletContext() {

		return null;
	}

	public HttpSessionContext getSessionContext() {

		return null;
	}

	public Object getValue(String arg0) {

		return null;
	}

	public String[] getValueNames() {

		return null;
	}

	public void invalidate() {

	}

	public boolean isNew() {

		return false;
	}

	public void putValue(String arg0, Object arg1) {

	}

	public void removeAttribute(String arg0) {

	}

	public void removeValue(String arg0) {

	}

	public void setAttribute(String arg0, Object arg1) {

	}

	public void setMaxInactiveInterval(int arg0) {

	}

}
