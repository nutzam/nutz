package org.nutz.http;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.nutz.http.Request.METHOD;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;

public class Http {

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

    public static Response get(String url) {
        return Sender.create(Request.get(url)).send();
    }

    public static Response get(String url, int timeout) {
        return Sender.create(Request.get(url)).setTimeout(timeout).send();
    }

    public static String post(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.create(url, METHOD.POST, params, null))
                     .setTimeout(timeout)
                     .send()
                     .getContent();
    }

    public static Response post2(String url, Map<String, Object> params, int timeout) {
        return Sender.create(Request.create(url, METHOD.POST, params, null))
                     .setTimeout(timeout)
                     .send();
    }

    public static String encode(Object s) {
        if (null == s)
            return "";
        try {
            // Fix issue 283, 按照“茶几”的意见再次修改
            return URLEncoder.encode(s.toString(), Encoding.CHARSET_UTF8.name());
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
                if (url.startsWith("http") && url.contains("://") && url.length() > "https://".length()) {
                    url = url.substring(url.indexOf("://") + "://".length());
                    if (url.startsWith("127.0.0") || url.startsWith("localhost"))
                        return null;
                }
                req.getHeader().set("Connection", "close");
                return getProxy(req.getUrl());
            }
        };
    }

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
                if (url.startsWith("http") && url.contains("://") && url.length() > "https://".length()) {
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
    
    /**
     * 禁用JVM的https证书验证机制, 例如访问12306, 360 openapi之类的自签名证书
     * @return 禁用成功与否
     */
    public static boolean disableJvmHttpsCheck() {
    	try {
			SSLContext sc = SSLContext.getInstance("SSL");
			TrustManager[] tmArr={new X509TrustManager(){
			    public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) throws CertificateException{}
			    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate,String paramString) throws CertificateException {}
			    public X509Certificate[] getAcceptedIssuers() {return null;}
			}};
			sc.init(null, tmArr, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			return true;
		} catch (KeyManagementException e) {
			return false;
		} catch (NoSuchAlgorithmException e) {
			return false;
		}
    }
}
