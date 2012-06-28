package org.nutz.http;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.nutz.lang.Encoding;

public class HttpDumper {
    public static void duplicateHttpHeaders(HttpServletRequest request, URLConnection conn) {
        Enumeration<?> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            conn.setRequestProperty(name, request.getHeader(name));
        }
    }

    public static String dumpInputStream(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(dumpHeaders(request));
        sb.append('\n');
        sb.append("<POSTDATA>");
        sb.append('\n');
        try {
            InputStreamReader in = new InputStreamReader(request.getInputStream(), Encoding.CHARSET_UTF8);
            int c;
            while ((c = in.read()) != -1) {
                sb.append((char) c);
            }
            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        sb.append('\n');
        sb.append("</POSTDATA>");
        sb.append('\n');
        return sb.toString();
    }

    public static String dumpData(HttpServletRequest request) {
        Enumeration<?> en = request.getParameterNames();
        StringBuilder sb = new StringBuilder();
        sb.append(dumpHeaders(request));
        sb.append('\n');
        sb.append("<DATA>");
        sb.append('\n');
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append(name + "=" + request.getParameter(name));
            sb.append('\n');
        }
        sb.append("</DATA>");
        sb.append('\n');
        return sb.toString();
    }

    static public String dumpHeaders(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> em = request.getHeaderNames();
        sb.append('\n');
        sb.append("<HEADERS request=\"" + request.getRequestURL().toString());
        if (null != request.getQueryString())
            sb.append("?" + request.getQueryString());
        sb.append("\"");
        sb.append('\n' + "SESSIONid=\"" + request.getSession().getId() + "\"");
        sb.append('\n' + "ServerName=\"" + request.getServerName() + "\"");
        sb.append('\n' + "ServerPort=\"" + request.getServerPort() + "\"");
        sb.append('\n' + "localAddr=\"" + request.getLocalAddr() + "\"");
        sb.append('\n' + "localName=\"" + request.getLocalName() + "\"");
        sb.append('\n' + "localPort=\"" + request.getLocalPort() + "\"");
        sb.append('\n' + "RemoteAddr=\"" + request.getRemoteAddr() + "\"");
        sb.append('\n' + "RemoteHost=\"" + request.getRemoteHost() + "\"");
        sb.append('\n' + "RemotePort=\"" + request.getRemotePort() + "\"");
        sb.append('\n' + "Encoding=\"" + request.getCharacterEncoding() + "\"");
        sb.append('\n' + "Method=\"" + request.getMethod() + "\"");
        sb.append(">");
        while (em.hasMoreElements()) {
            String name = (String) em.nextElement();
            sb.append('\n');
            sb.append("[" + name + "]:");
            sb.append(request.getHeader(name));
        }
        sb.append('\n');
        sb.append("</HEADERS>");

        return sb.toString();
    }

    public static String dumpHeaders(URLConnection conn) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = conn.getHeaderFields().keySet().iterator();
        sb.append('\n');
        sb.append("<HEADERS url=\"" + conn.getURL().toString() + "\">");
        while (it.hasNext()) {
            String name = (String) it.next();
            sb.append('\n');
            sb.append("[" + name + "]:");
            sb.append(conn.getHeaderField(name));
        }
        sb.append('\n');
        sb.append("</HEADERS>");
        return sb.toString();
    }

    public static String dumpSessionAttributes(HttpSession session) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> en = session.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append("[" + name + "]: " + session.getAttribute(name) + '\n');
        }
        return sb.toString();
    }

    public static String dumpContextAttributes(ServletContext context) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> en = context.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append("[" + name + "]: " + context.getAttribute(name) + '\n');
        }
        return sb.toString();
    }

    public static String dumpRequestAttributes(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> en = request.getAttributeNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append("[" + name + "]: " + request.getAttribute(name) + '\n');
        }
        return sb.toString();
    }

    public static String dumpRequestParams(ServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append("[" + name + "]: " + request.getParameter(name) + '\n');
        }
        return sb.toString();
    }

    public static String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    public static String dumpBrief(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        Enumeration<?> en = request.getParameterNames();
        while (en.hasMoreElements()) {
            String name = (String) en.nextElement();
            sb.append(name);
            String v = request.getParameter(name);
            if (v != null) {
                sb.append("=");
                sb.append(v);
            }
            if (en.hasMoreElements())
                sb.append("&");
        }
        return String.format(    "%5s:%15s[%5s]%s",
                                new Object[]{    request.getLocale().toString(),
                                                request.getRemoteAddr(),
                                                ((HttpServletRequest) request).getMethod(),
                                                ((HttpServletRequest) request)    .getRequestURL()
                                                                                .toString()
                                                        + (sb.length() > 0    ? "?" + sb.toString()
                                                                            : "")});
    }
}
