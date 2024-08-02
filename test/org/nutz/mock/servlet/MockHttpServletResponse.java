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

import org.nutz.castor.Castors;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.mock.Mock;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponseWrapper;

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

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public void addDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    @Override
    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void addIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    @Override
    public boolean containsHeader(String key) {
        return headers.containsKey(key);
    }

    @Override
    public void sendError(int error) throws IOException {
        this.setStatus(error);
    }

    @Override
    public void sendError(int arg0, String arg1) throws IOException {
        this.setStatus(arg0, arg1);
    }

    @Override
    public void sendRedirect(String value) throws IOException {
        headers.put("Location", "" + value);
    }

    @Override
    public void setDateHeader(String key, long value) {
        headers.put(key, "" + value);
    }

    @Override
    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    @Override
    public void setIntHeader(String key, int value) {
        headers.put(key, "" + value);
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int status, String statusMessage) {
        this.status = status;
        this.statusMessage = statusMessage;
    }

    @Override
    public void flushBuffer() throws IOException {
        getWriter().flush();
    }

    @Override
    public int getBufferSize() {
        return stream.size();
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return new ServletOutputStream() {

            @Override
            public void write(int arg0) throws IOException {
                stream.write(arg0);
            }

            @Override
            public boolean isReady() {

                return false;
            }

            @Override
            public void setWriteListener(WriteListener writeListener) {}
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(new OutputStreamWriter(stream, characterEncoding));
        }
        return writer;
    }

    @Override
    public void reset() {
        stream.reset();
    }

    @Override
    public void resetBuffer() {
        stream.reset();
    }

    protected String characterEncoding = Encoding.defaultEncoding();

    @Override
    public void setCharacterEncoding(String characterEncoding) {
        this.characterEncoding = characterEncoding;
    }

    @Override
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
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

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public boolean isCommitted() {
        return false;
    }
}
