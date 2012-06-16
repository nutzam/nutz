package org.nutz.http.server.impl.servlet;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.http.impl.NutHttpReq;
import org.nutz.http.impl.SessionManger;
import org.nutz.lang.Lang;

public class NutHttpServletRequest extends NutHttpReq implements HttpServletRequest {
	
	public String getContextPath() {
		return getServletContext().getContextPath();
	}
	//----------------------------------------------------------------
	@Override
	public Enumeration<Locale> getLocales() {
		throw Lang.noImplement();
	}
	@Override
	public String getLocalName() {
		return locale.getDisplayName();
	}
	
	//----------------------------------------------------------------
	// Sesssion 相关
	private SessionManger sessionManger;
	@Override
	public HttpSession getSession() {
		return sessionManger.get(this,true);
	}
	@Override
	public HttpSession getSession(boolean createIfNotExist) {
		return sessionManger.get(this,createIfNotExist);
	}
	//----------------------------------------------------------------
	@Override
	public RequestDispatcher getRequestDispatcher(String paramString) {
		throw Lang.noImplement();
	}

	//-----------------------------------------------------------
	//这以下的,是不打算实现的API
	

	@Override
	public String getAuthType() {
		throw Lang.noImplement();
	}
	@Override
	public String getLocalAddr() {
		throw Lang.noImplement();
	}
	@Override
	public Principal getUserPrincipal() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isRequestedSessionIdFromURL() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isRequestedSessionIdValid() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isSecure() {
		throw Lang.noImplement();
	}
	@Override
	public boolean isUserInRole(String paramString) {
		throw Lang.noImplement();
	}
	@Override
	public int getLocalPort() {
		throw Lang.noImplement();
	}
	@Override
	public String getRequestedSessionId() {
		throw Lang.noImplement();
	}
	

	@Override
	public int getRemotePort() {
		throw Lang.noImplement();
	}
	@Override
	public String getRemoteHost() {
		throw Lang.noImplement();
	}
	@Override
	public String getRemoteAddr() {
		throw Lang.noImplement();
	}
	@Override
	public String getRemoteUser() {
		throw Lang.noImplement();
	}
	@Override
	public String getServerName() {
		throw Lang.noImplement();
	}
	@Override
	public int getServerPort() {
		throw Lang.noImplement();
	}
	@Override
	public String getRealPath(String paramString) {
		throw Lang.noImplement();
	}
	@Override
	public String getServletPath() {
		return requestURI.substring(getContextPath().length());
	}
	
}
