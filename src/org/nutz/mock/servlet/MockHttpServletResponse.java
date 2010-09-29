package org.nutz.mock.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;

public class MockHttpServletResponse implements HttpServletResponse {
	
	protected ByteArrayOutputStream stream = new ByteArrayOutputStream();
	
	protected PrintWriter writer;
	
	protected Map<String, String> headers;
	
	protected Set<Cookie> cookies;
	
	protected int status;
	
	protected Locale locale;
	
	public MockHttpServletResponse() {
		headers = new HashMap<String, String>();
		cookies = new HashSet<Cookie>();
		status = 200;
	}

	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	public void addDateHeader(String key, long value) {
		headers.put(key, ""+value);
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

	public String encodeRedirectURL(String arg0) {
		throw Lang.noImplement();
	}

	public String encodeRedirectUrl(String arg0) {
		throw Lang.noImplement();
	}

	public String encodeURL(String arg0) {
		throw Lang.noImplement();
	}

	public String encodeUrl(String arg0) {
		throw Lang.noImplement();
	}

	public void sendError(int error) throws IOException {
		status = error;
	}

	public void sendError(int arg0, String arg1) throws IOException {
		throw Lang.noImplement();
	}

	public void sendRedirect(String value) throws IOException {
		headers.put("Location", ""+value);
	}

	public void setDateHeader(String key, long value) {
		headers.put(key, ""+value);
	}

	public void setHeader(String key, String value) {
		headers.put(key, value);
	}

	public void setIntHeader(String key, int value) {
		headers.put(key, ""+value);
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setStatus(int arg0, String arg1) {
		throw Lang.noImplement();
	}

	public void flushBuffer() throws IOException {
		getWriter().flush();
	}

	public int getBufferSize() {
		return stream.size();
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public String getContentType() {
		return contentType;
	}

	public Locale getLocale() {
		return locale;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw Lang.noImplement();
	}

	public PrintWriter getWriter() throws IOException {
		if (writer == null){
			writer = new PrintWriter(new OutputStreamWriter(stream, characterEncoding));
		}
		return writer;
	}

	public boolean isCommitted() {
		throw Lang.noImplement();
	}

	public void reset() {
		stream.reset();
	}

	public void resetBuffer() {
		stream.reset();
	}

	public void setBufferSize(int arg0) {
		throw Lang.noImplement();
	}

	protected String characterEncoding = Encoding.defaultEncoding();
	
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public void setContentLength(int arg0) {
		throw Lang.noImplement();
	}

	protected String contentType;
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getContentAsString() {
		try {
			getWriter().flush();
			return stream.toString(characterEncoding);
		} 
		catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public String getHeader(String key){
		return headers.get(key);
	}
}
