package org.nutz.mock.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;

public class MockHttpServletResponse implements HttpServletResponse {
	
	protected ByteArrayOutputStream stream = new ByteArrayOutputStream();
	
	protected PrintWriter writer = new PrintWriter(new OutputStreamWriter(stream));

	public void addCookie(Cookie cookie) {
		throw Lang.noImplement();
	}

	public void addDateHeader(String arg0, long arg1) {
		throw Lang.noImplement();
	}

	public void addHeader(String arg0, String arg1) {
		throw Lang.noImplement();
	}

	public void addIntHeader(String arg0, int arg1) {
		throw Lang.noImplement();
	}

	public boolean containsHeader(String arg0) {
		throw Lang.noImplement();
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

	public void sendError(int arg0) throws IOException {
		throw Lang.noImplement();
	}

	public void sendError(int arg0, String arg1) throws IOException {
		throw Lang.noImplement();
	}

	public void sendRedirect(String arg0) throws IOException {
		throw Lang.noImplement();
	}

	public void setDateHeader(String arg0, long arg1) {
		throw Lang.noImplement();
	}

	public void setHeader(String arg0, String arg1) {
//		throw Lang.noImplement();
	}

	public void setIntHeader(String arg0, int arg1) {
		throw Lang.noImplement();
	}

	public void setStatus(int arg0) {
//		throw Lang.noImplement();
	}

	public void setStatus(int arg0, String arg1) {
		throw Lang.noImplement();
	}

	public void flushBuffer() throws IOException {
		writer.flush();
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
		throw Lang.noImplement();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		throw Lang.noImplement();
	}

	public PrintWriter getWriter() throws IOException {
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

	protected String characterEncoding = "UTF-8";
	
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

	public void setLocale(Locale arg0) {
		throw Lang.noImplement();
	}

	public String getContentAsString() {
		try {
			writer.flush();
			return stream.toString(characterEncoding);
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

}
