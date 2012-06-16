package org.nutz.http.server.impl.servlet;

import javax.servlet.http.HttpServletResponse;

import org.nutz.http.impl.NutHttpResp;
import org.nutz.lang.Lang;

public class NutHttpServletRespose extends NutHttpResp implements HttpServletResponse {

	//---------------------------------------------------------
	@Override
	public String encodeRedirectUrl(String paramString) {
		throw Lang.noImplement();
	}
	@Override
	public String encodeRedirectURL(String paramString) {
		throw Lang.noImplement();
	}
	@Override
	public String encodeUrl(String paramString) {
		throw Lang.noImplement();
	}
	@Override
	public String encodeURL(String paramString) {
		throw Lang.noImplement();
	}
}
