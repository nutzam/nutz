package org.nutz.http.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;

import org.nutz.lang.Lang;

public class NutHttpResp extends AbstractHttpObject2 {

	//------------------------------------------------
	protected int status;
	public void setStatus(int status) {
		this.status = status;
	}
	//----------------------------------------------------
	// add header
	public void addDateHeader(String key, long value) {
		headers.put(key, Https.httpDate(new Date(value)));
	}
	public void addHeader(String key, String value) {
		headers.put(key, value);
	}
	public void addIntHeader(String key, int value) {
		headers.put(key, ""+value);
	}
	public boolean containsHeader(String key) {
		return headers.containsKey(key);
	}
	public void setDateHeader(String key, long value) {
		headers.put(key, Https.httpDate(new Date(value)));
	}
	public void setIntHeader(String key, int value) {
		headers.put(key, ""+value);
	}
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	public void setContentLength(int len) {
		headers.put("Content-Length", ""+len);
	}
	public void setContentType(String type) {
		headers.put("Content-Type", type);
	}
	public String getContentType() {
		return headers.get("Content-Type");
	}
	//---------------------------------------------------
	protected List<Cookie> cookies = new ArrayList<Cookie>();
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}
	
	//---------------------------------------------------
	// Http Action
	protected boolean committed;
	
	public boolean isCommitted() {
		return committed;
	}
	public void sendError(int errNo) throws IOException {
		throw Lang.noImplement();
	}
	
	public void sendError(int errNo, String msg) throws IOException {
		throw Lang.noImplement();
	}
	
	public void sendRedirect(String path) throws IOException {
		throw Lang.noImplement();
	}
	
	public void flushBuffer() throws IOException {
		throw Lang.noImplement();
	}
	
	public void reset() {
		throw Lang.noImplement();
	}
	
	public void resetBuffer() {
		throw Lang.noImplement();
	}
	
	public int getBufferSize() {
		return 8192;
	}
	
	public void setBufferSize(int paramInt) {
		//just pass
	}
	
	public void setStatus(int paramInt, String paramString) {
		throw Lang.noImplement();
	}
	//------------------------------------------------------
	protected ServletOutputStream out;
	protected boolean canGetOutputStream = true;
	
	public ServletOutputStream getOutputStream() throws IOException {
		if (canGetOutputStream) {
			canGetOutputStream = false;
			return out;
		}
		throw new IllegalStateException();
	}
	
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(getOutputStream());
	}
	//---------------------------------------------------------
	protected String characterEncoding = "UTF-8";
	
	public String getCharacterEncoding() {
		return characterEncoding;
	}
	
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}
	//---------------------------------------------------------
}
