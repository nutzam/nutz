package org.nutz.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;

public class Request {

    public static enum METHOD {
        GET, POST, OPTIONS, PUT, DELETE, TRACE, CONNECT, HEAD
    }

    public static Request get(String url) {
        return create(url, METHOD.GET, new HashMap<String, Object>());
    }

    public static Request get(String url, Header header) {
        return Request.create(url, METHOD.GET, new HashMap<String, Object>(), header);
    }

    public static Request post(String url) {
        return create(url, METHOD.POST, new HashMap<String, Object>());
    }

    public static Request post(String url, Header header) {
        return Request.create(url, METHOD.POST, new HashMap<String, Object>(), header);
    }

    public static Request create(String url, METHOD method) {
        return create(url, method, new HashMap<String, Object>());
    }

    @SuppressWarnings("unchecked")
    public static Request create(String url, METHOD method, String paramsAsJson, Header header) {
        return create(url, method, (Map<String, Object>) Json.fromJson(paramsAsJson), header);
    }

    @SuppressWarnings("unchecked")
    public static Request create(String url, METHOD method, String paramsAsJson) {
        return create(url, method, (Map<String, Object>) Json.fromJson(paramsAsJson));
    }

    public static Request create(String url, METHOD method, Map<String, Object> params) {
        return Request.create(url, method, params, Header.create());
    }

    public static Request create(String url,
                                 METHOD method,
                                 Map<String, Object> params,
                                 Header header) {
        return new Request().setMethod(method).setParams(params).setUrl(url).setHeader(header);
    }

    private Request() {}

    private String url;
    private METHOD method;
    private String methodString;
    private Header header;
    private Map<String, Object> params;
    private byte[] data;
    private URL cacheUrl;
    private InputStream inputStream;
    private String enc = Encoding.UTF8;
    private boolean offEncode;

    public Request offEncode(boolean off) {
        this.offEncode = off;
        return this;
    }

    public URL getUrl() {
        if (cacheUrl != null) {
            return cacheUrl;
        }

        StringBuilder sb = new StringBuilder(url);
        try {
            if (this.isGet() && null != params && params.size() > 0) {
                sb.append(url.indexOf('?') > 0 ? '&' : '?');
                sb.append(getURLEncodedParams());
            }
            cacheUrl = new URL(sb.toString());
            return cacheUrl;
        }
        catch (Exception e) {
            throw new HttpException(sb.toString(), e);
        }
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public String getURLEncodedParams() {
        final StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Entry<String, Object> en : params.entrySet()) {
                final String key = en.getKey();
                Object val = en.getValue();
                if (val == null)
                    val = "";
                Lang.each(val, new Each<Object>() {
                    public void invoke(int index, Object ele, int length)
                            throws ExitLoop, ContinueLoop, LoopException {
                        if (offEncode) {
                            sb.append(key).append('=').append(ele).append('&');
                        } else {
                            sb.append(Http.encode(key, enc))
                              .append('=')
                              .append(Http.encode(ele, enc))
                              .append('&');
                        }
                    }
                });
            }
            if (sb.length() > 0)
                sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public InputStream getInputStream() {
        if (inputStream != null) {
            return inputStream;
        } else {
            if (header.get("Content-Type") == null)
                header.asFormContentType(enc);
            if (null == data) {
                try {
                    return new ByteArrayInputStream(getURLEncodedParams().getBytes(enc));
                }
                catch (UnsupportedEncodingException e) {
                    throw Lang.wrapThrow(e);
                }
            }
            return new ByteArrayInputStream(data);
        }
    }

    public Request setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public Request setData(byte[] data) {
        this.data = data;
        return this;
    }

    public Request setData(String data) {
        try {
            this.data = data.getBytes(Encoding.UTF8);
        }
        catch (UnsupportedEncodingException e) {
            // 不可能
        }
        return this;
    }

    public Request setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Request setUrl(String url) {
        if (url != null && !url.contains("://"))
            // 默认采用http协议
            this.url = "http://" + url;
        else
            this.url = url;
        return this;
    }

    public METHOD getMethod() {
        return method;
    }

    public boolean isGet() {
        return METHOD.GET == method;
    }

    public boolean isPost() {
        return METHOD.POST == method;
    }

    public boolean isDelete() {
        return METHOD.DELETE == method;
    }

    public boolean isPut() {
        return METHOD.PUT == method;
    }

    public Request setMethod(METHOD method) {
        this.method = method;
        return this;
    }

    public Header getHeader() {
        return header;
    }

    public Request setHeader(Header header) {
        if (header == null)
            header = new Header();
        this.header = header;
        return this;
    }

    public Request setCookie(Cookie cookie) {
        header.set("Cookie", cookie.toString());
        return this;
    }

    public Cookie getCookie() {
        String s = header.get("Cookie");
        if (null == s)
            return new Cookie();
        return new Cookie(s);
    }

    /**
     * 设置发送内容的编码,仅对String或者Map<String,Object>类型的data有效
     */
    public Request setEnc(String reqEnc) {
        if (reqEnc != null)
            this.enc = reqEnc;
        return this;
    }

    public String getEnc() {
        return enc;
    }

    public Request header(String key, String value) {
        getHeader().set(key, value);
        return this;
    }

    public Request setMethodString(String methodString) {
        try {
            method = METHOD.valueOf(methodString.toUpperCase());
        }
        catch (Throwable e) {
            this.methodString = methodString;
        }
        return this;
    }

    public String getMethodString() {
        return methodString;
    }
}
