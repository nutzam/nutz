package org.nutz.http;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Request {

    public static enum METHOD {
        GET, POST, OPTIONS, PUT, DELETE, TRACE, CONNECT
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
    private Header header;
    private Map<String, Object> params;
    private byte[] data;
    private URL cacheUrl;
    private InputStream inputStream;
    private String enc;

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
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
                String key = it.next();
                sb.append(Http.encode(key)).append('=').append(Http.encode(params.get(key)));
                if (it.hasNext())
                    sb.append('&');
            }
        }
        return sb.toString();
    }

    public InputStream getInputStream() {
        if (inputStream != null) {
            return inputStream;
        } else {
            if (null == data) {
                if (enc != null)
                    try {
                        return new ByteArrayInputStream(getURLEncodedParams().getBytes(enc));
                    }
                    catch (UnsupportedEncodingException e) {
                        throw Lang.wrapThrow(e);
                    }
                return new ByteArrayInputStream(Strings.getBytesUTF8(getURLEncodedParams()));
            }
            return new ByteArrayInputStream(data);
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setData(String data) {
        try {
            this.data = data.getBytes(Encoding.UTF8);
        }
        catch (UnsupportedEncodingException e) {
            // 不可能
        }
    }

    private Request setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }

    public Request setUrl(String url) {
        if (url != null && url.indexOf("://") < 0)
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
        this.enc = reqEnc;
        return this;
    }
}
