package org.nutz.http;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.nutz.Nutz;
import org.nutz.http.Request.METHOD;
import org.nutz.http.sender.FilePostSender;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Http {

    /**
     * @see https://en.wikipedia.org/wiki/List_of_HTTP_status_codes
     */
    private static final Map<String, String> code_text = new HashMap<String, String>();

    static {
        code_text.put("100", "Continue");
        code_text.put("101", "Switching Protocols");
        code_text.put("102", "Processing");
        code_text.put("200", "OK");
        code_text.put("201", "Created");
        code_text.put("202", "Accepted");
        code_text.put("203", "Non-Authoritative Information"); // (since HTTP/1.1)
        code_text.put("204", "No Content");
        code_text.put("205", "Reset Content");
        code_text.put("206", "Partial Content");
        code_text.put("207", "Multi-Status");
        code_text.put("208", "Already Reported");
        code_text.put("226", "IM Used");

        code_text.put("301", "Moved Permanently");
        code_text.put("302", "Found");
        code_text.put("303", "See Other"); // (since HTTP/1.1)
        code_text.put("304", "Not Modified");
        code_text.put("305", "Use Proxy"); // (since HTTP/1.1)
        code_text.put("306", "Switch Proxy");
        code_text.put("307", "Temporary Redirect"); // (since HTTP/1.1)
        code_text.put("308", "Permanent Redirect");

        code_text.put("400", "Bad Request");
        code_text.put("401", "Unauthorized");
        code_text.put("402", "Payment Required");
        code_text.put("403", "Forbidden");
        code_text.put("404", "Not Found");
        code_text.put("405", "Method Not Allowed");
        code_text.put("406", "Not Acceptable");
        code_text.put("407", "Proxy Authentication Required ");
        code_text.put("408", "Request Timeout");
        code_text.put("409", "Conflict");
        code_text.put("410", "Gone");
        code_text.put("411", "Length Required");
        code_text.put("412", "Precondition Failed");
        code_text.put("413", "Payload Too Large");
        code_text.put("414", "URI Too Long");
        code_text.put("415", "Unsupported Media Type");
        code_text.put("416", "Range Not Satisfiable");
        code_text.put("417", "Expectation Failed");
        // ... 后面的 "4xx" 都是什么鬼 -_-! 先不抄了吧

        code_text.put("500", "Internal Server Error");
        code_text.put("501", "Not Implemented");
        code_text.put("502", "Bad Gateway");
        code_text.put("503", "Service Unavailable");
        code_text.put("504", "Gateway Timeout");
        code_text.put("505", "HTTP Version Not Supported");
        code_text.put("506", "Variant Also Negotiates");
        code_text.put("507", "Insufficient Storage ");
        code_text.put("508", "Loop Detected");
        code_text.put("509", "Bandwidth Limit Exceeded ");
        code_text.put("510", "Not Extended ");
        code_text.put("511", "Network Authentication Required");
        code_text.put("520", "Unknown Error");
        code_text.put("522", "Origin Connection Time-out");
        code_text.put("598", "Network read timeout error ");
        code_text.put("599", "Network connect timeout error");
    }

    public static String getStatusText(int statusCode) {
        return code_text.get(""+statusCode);
    }

    public static String getStatusText(int statusCode, String dft) {
        return Strings.sNull(code_text.get(""+statusCode), dft);
    }

    public static class multipart {
        public static String getBoundary(String contentType) {
            if (null == contentType)
                return null;
            for (String tmp : contentType.split(";")) {
                tmp = tmp.trim();
                if (tmp.startsWith("boundary=")) {
                    return tmp.substring("boundary=".length());
                }
            }
            return null;
        }

        public static String formatName(String name, String filename, String contentType) {
            StringBuilder sb = new StringBuilder();
            sb.append("Content-Disposition: form-data; name=\"");
            sb.append(name);
            sb.append("\"");
            if (null != filename)
                sb.append("; filename=\"" + filename + "\"");
            if (null != contentType)
                sb.append("\nContent-Type: " + contentType);
            sb.append('\n' + '\n');
            return sb.toString();
        }

        public static String formatName(String name) {
            return formatName(name, null, null);
        }
    }

    /**
     * 访问一个URL
     * 
     * @param url
     *            需要访问的URL
     * @return http响应
     */
    public static Response get(String url) {
        return Sender.create(Request.get(url)).send();
    }

    /**
     * 访问一个URL,并设置超时
     * 
     * @param url
     *            需要访问的URL
     * @param timeout
     *            超时设置,单位为毫秒
     * @return http响应
     */
    public static Response get(String url, int timeout) {
        return Sender.create(Request.get(url)).setTimeout(timeout).send();
    }
    
    public static Response get(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.get(url).setParams(params)).setTimeout(timeout).send();
    }
    
    public static Response get(String url, Header header, int timeout) {
        return Sender.create(Request.get(url).setHeader(header)).setTimeout(timeout).send();
    }

    /**
     * 访问一个URL,并设置超时及参数
     * 
     * @param url
     *            需要访问的URL
     * @param params
     *            参数
     * @param timeout
     *            超时设置,单位为毫秒
     * @return http响应
     */
    public static String post(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.create(url, METHOD.POST, params, null))
                     .setTimeout(timeout)
                     .send()
                     .getContent();
    }

    /**
     * 访问一个URL,并设置超时及参数
     * 
     * @param url
     *            需要访问的URL
     * @param params
     *            参数
     * @param timeout
     *            超时设置,单位为毫秒
     * @return http响应
     */
    public static Response post2(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.create(url, METHOD.POST, params, null))
                     .setTimeout(timeout)
                     .send();
    }

    public static Response post3(String url, Object body, Header header, int timeout) {
        Request req = Request.create(url, METHOD.POST).setHeader(header);
        if (body != null) {
            if (body instanceof InputStream) {
                req.setInputStream((InputStream) body);
            } else if (body instanceof byte[]) {
                req.setData((byte[]) body);
            } else {
                req.setData(String.valueOf(body));
            }
        }
        return Sender.create(req).setTimeout(timeout).send();
    }

    public static Response upload(String url,
                                  Map<String, Object> params,
                                  Header header,
                                  int timeout) {
        Request req = Request.create(url, METHOD.POST, params, header);
        return new FilePostSender(req).setTimeout(timeout).send();
    }

    public static String encode(Object s) {
        return encode(s, null);
    }

    public static String encode(Object s, String enc) {
        if (null == s)
            return "";
        try {
            // Fix issue 283, 按照“茶几”的意见再次修改
            return URLEncoder.encode(s.toString(),
                                     enc == null ? Encoding.CHARSET_UTF8.name() : enc);
        }
        catch (UnsupportedEncodingException e) {
            throw Lang.wrapThrow(e);
        }
    }

    public static String post(String url, Map<String, Object> params, String inenc, String reenc) {
        return Sender.create(Request.create(url, METHOD.POST, params, null).setEnc(inenc))
                     .send()
                     .getContent(reenc);
    }
    
    public static Response postXML(String url, String xml, int timeout) {
        Request req = Request.create(url, METHOD.POST);
        req.setData(xml);
        req.getHeader().set("Content-Type", "application/xml");
        return Sender.create(req).setTimeout(timeout).send();
    }

    protected static ProxySwitcher proxySwitcher;

    protected static boolean autoSwitch;

    public static void setAutoSwitch(boolean use) {
        autoSwitch = use;
    }

    public static void setHttpProxy(String host, int port) {
        final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
        proxySwitcher = new ProxySwitcher() {
            public Proxy getProxy(URL url) {
                return proxy;
            }

            public Proxy getProxy(Request req) {
                if ("close".equals(req.getHeader().get("NoProxy")))
                    return null;
                String url = req.getUrl().toString();
                if (url.startsWith("http")
                    && url.contains("://")
                    && url.length() > "https://".length()) {
                    url = url.substring(url.indexOf("://") + "://".length());
                    if (url.startsWith("127.0.0") || url.startsWith("localhost"))
                        return null;
                }
                req.getHeader().set("Connection", "close");
                return getProxy(req.getUrl());
            }
        };
    }

    /*
    * please use setSocketProxy method
    */
    @Deprecated
    public static void setSocktProxy(String host, int port) {
        final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
        proxySwitcher = new ProxySwitcher() {
            public Proxy getProxy(URL url) {
                return proxy;
            }

            public Proxy getProxy(Request req) {
                if ("close".equals(req.getHeader().get("NoProxy")))
                    return null;
                String url = req.getUrl().toString();
                if (url.startsWith("http")
                        && url.contains("://")
                        && url.length() > "https://".length()) {
                    url = url.substring(url.indexOf("://"));
                    if (url.startsWith("127.0.0") || url.startsWith("localhost"))
                        return null;
                }
                req.getHeader().set("Connection", "close");
                return getProxy(req.getUrl());
            }
        };
    }

    public static void setSocketProxy(String host, int port) {
        final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
        proxySwitcher = new ProxySwitcher() {
            public Proxy getProxy(URL url) {
                return proxy;
            }

            public Proxy getProxy(Request req) {
                if ("close".equals(req.getHeader().get("NoProxy")))
                    return null;
                String url = req.getUrl().toString();
                if (url.startsWith("http")
                    && url.contains("://")
                    && url.length() > "https://".length()) {
                    url = url.substring(url.indexOf("://"));
                    if (url.startsWith("127.0.0") || url.startsWith("localhost"))
                        return null;
                }
                req.getHeader().set("Connection", "close");
                return getProxy(req.getUrl());
            }
        };
    }

    public static ProxySwitcher getProxySwitcher() {
        return proxySwitcher;
    }

    public static void setProxySwitcher(ProxySwitcher proxySwitcher) {
        Http.proxySwitcher = proxySwitcher;
    }
    
    protected static SSLSocketFactory sslSocketFactory;

    /**
     * 禁用JVM的https证书验证机制, 例如访问12306, 360 openapi之类的自签名证书
     * 
     * @return 禁用成功与否
     */
    public static boolean disableJvmHttpsCheck() {
        try {
            setSSLSocketFactory(nopSSLSocketFactory());
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public static SSLSocketFactory nopSSLSocketFactory() throws Exception {
        SSLContext sc = SSLContext.getInstance("SSL");
        TrustManager[] tmArr = {new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {}

            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {}

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }};
        sc.init(null, tmArr, new SecureRandom());
        return sc.getSocketFactory();
    }
    
    public static void setSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        Http.sslSocketFactory = sslSocketFactory;
    }
    
    public static HashMap<String, String> DEFAULT_HEADERS = new HashMap<String, String>();
    static {

        DEFAULT_HEADERS.put("User-Agent", "Nutz.Robot " + Nutz.version());
        DEFAULT_HEADERS.put("Accept-Encoding", "gzip,deflate");
        DEFAULT_HEADERS.put("Accept", "text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
        DEFAULT_HEADERS.put("Accept-Language", "en-US,en,zh,zh-CN");
        DEFAULT_HEADERS.put("Accept-Charset", "ISO-8859-1,*,utf-8");
        DEFAULT_HEADERS.put("Connection", "keep-alive");
        DEFAULT_HEADERS.put("Cache-Control", "max-age=0");
    }
}
