package org.nutz.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Cookie implements HttpReqRespInterceptor {

    private static final Log log = Logs.get();
    
    protected Map<String, String> map;
    
    protected boolean debug;

    public Cookie() {
        map = new HashMap<String, String>();
    }

    public Cookie(String s) {
        this();
        parse(s);
    }

    public String get(String name) {
        return map.get(name);
    }

    public Cookie remove(String name) {
        map.remove(name);
        return this;
    }

    public Cookie set(String name, String value) {
        map.put(name, value);
        return this;
    }

    public void parse(String str) {
        if (debug)
            log.debug("parse " + str);
        String[] ss = Strings.splitIgnoreBlank(str, ";");
        for (String s : ss) {
            Pair<String> p = Pair.create(Strings.trim(s));
            if (p.getValueString() == null)
                continue;
            if ("Path".equals(p.getName()) || "Expires".equals(p.getName()))
                continue;
            if ("Max-Age".equals(p.getName())) {
                long age = Long.parseLong(p.getValue());
                if (age == 0)
                    return;
            }
            String val = p.getValueString();
            if (debug)
                log.debugf("add cookie [%s=%s]",  p.getName(), val);
            map.put(p.getName(), val);
        }
    }

    public String toString() {
        if (map.isEmpty())
            return "";
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> en : map.entrySet()) {
            sb.append(en.getKey()).append('=').append(en.getValue()).append("; ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public void beforeConnect(Request request) {
    }
    
    public void afterConnect(Request request, HttpURLConnection conn) {
        if (this.map.isEmpty())
            return;
        String c = toString();
        if (debug)
            log.debugf("add Cookie for req [%s]", c);
        if (!Strings.isBlank(c))
            conn.addRequestProperty("Cookie", c);
    }
    
    public void afterResponse(Request request, HttpURLConnection conn, Response response) {
        Map<String, List<String>> props = conn.getHeaderFields();
        for (Entry<String, List<String>> en : props.entrySet()) {
            if (en.getKey() == null || !en.getKey().equalsIgnoreCase("Set-Cookie")) {
                continue;
            }
            for (String e : en.getValue()) {
                if (debug)
                    log.debugf("found Set-Cookie [%s]", e);
                this.parse(e);
            }
            break;
        }
    }
    
    public String toJson() {
        return Json.toJson(map);
    }
    
    public int size() {
        return map.size();
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
