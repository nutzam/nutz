package org.nutz.http;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.nutz.http.Request.METHOD;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;

public class Http {

    public static class multipart {
        public static String getBoundary(String contentType) {
            if (null == contentType)
                return null;
            int pos = contentType.indexOf(";");
            if (pos <= 10)
                return null;
            if (!contentType.substring(0, pos).equalsIgnoreCase("multipart/form-data"))
                return null;
            pos = contentType.indexOf("=", pos);
            if (pos < 0)
                return null;
            return contentType.substring(pos + 1);
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
        return Sender.create(Request.create(url, METHOD.POST, params, null)).setTimeout(timeout).send().getContent();
    }
    
    public static String post(String url, Map<String, Object> params, String inenc, String reenc) {
        return Sender.create(Request.create(url, METHOD.POST, params, null)).send().getContent();
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
    
    public static ProxySwitcher proxySwitcher;
    
    public static void setHttpProxy(String host, int port) {
    	final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
    	proxySwitcher = new ProxySwitcher() {
    		public Proxy getProxy(URL url) {
    			return proxy;
    		}
    	};
    }
    
    public static void setSocktProxy(String host, int port) {
    	final Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));
    	proxySwitcher = new ProxySwitcher() {
    		public Proxy getProxy(URL url) {
    			return proxy;
    		}
    	};
    }
    
    public static ProxySwitcher getProxySwitcher() {
		return proxySwitcher;
	}
    
    public static void setProxySwitcher(ProxySwitcher proxySwitcher) {
		Http.proxySwitcher = proxySwitcher;
	}
}
