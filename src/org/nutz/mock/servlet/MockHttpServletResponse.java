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
import javax.servlet.http.HttpServletResponseWrapper;

import org.nutz.castor.Castors;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;

public class MockHttpServletResponse extends HttpServletResponseWrapper {

    protected ByteArrayOutputStream stream = new ByteArrayOutputStream();

    protected PrintWriter writer;

    protected Map<String, String> headers;

    protected Set<Cookie> cookies;

    protected int status;

    protected String statusMessage;

    protected Locale locale;

    protected String contentType;

    public MockHttpServletResponse() {
        super(Mock.EmtryHttpServletResponse);
        headers = new HashMap<String, String>();
        cookies = new HashSet<Cookie>();
        status = 200;
        statusMessage = "OK";
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    public void addDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    public void sendError(int error) throws IOException {
        this.setStatus(error);
    }

    public void sendError(int arg0, String arg1) throws IOException {
        this.setStatus(arg0, arg1);
    }

    public void sendRedirect(String value) throws IOException {
        headers.put("Location", "" + value);
    }

    public void setDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
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
        return new ServletOutputStream() {

            @Override
            public void write(int arg0) throws IOException {
                stream.write(arg0);
            }
        };
    }

    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, characterEncoding));
        }
        return writer;
    }

    public void reset() {
        stream.reset();
    }

    public void resetBuffer() {
        stream.reset();
    }

    protected String characterEncoding = Encoding.defaultEncoding();

    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getAsString() {
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

    public int getAsInt() {
        return Integer.parseInt(getAsString());
    }

    public long getAsLong() {
        return Long.parseLong(getAsString());
    }

    public <T> T getAs(Class<T> type) {
        return Castors.me().castTo(getAsString(), type);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

}
