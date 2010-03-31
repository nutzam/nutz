package org.nutz.mvc.upload;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings( { "unchecked", "deprecation" })
public class MockHttpSession implements HttpSession {

	@Override
	public Object getAttribute(String arg0) {

		return null;
	}

	@Override
	public Enumeration getAttributeNames() {

		return null;
	}

	@Override
	public long getCreationTime() {

		return 0;
	}

	@Override
	public String getId() {

		return null;
	}

	@Override
	public long getLastAccessedTime() {

		return 0;
	}

	@Override
	public int getMaxInactiveInterval() {

		return 0;
	}

	@Override
	public ServletContext getServletContext() {

		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {

		return null;
	}

	@Override
	public Object getValue(String arg0) {

		return null;
	}

	@Override
	public String[] getValueNames() {

		return null;
	}

	@Override
	public void invalidate() {

	}

	@Override
	public boolean isNew() {

		return false;
	}

	@Override
	public void putValue(String arg0, Object arg1) {

	}

	@Override
	public void removeAttribute(String arg0) {

	}

	@Override
	public void removeValue(String arg0) {

	}

	@Override
	public void setAttribute(String arg0, Object arg1) {

	}

	@Override
	public void setMaxInactiveInterval(int arg0) {

	}

}
