package org.nutz.http;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Pair;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Cookie implements HttpReqRespInterceptor {

    private static final Log log = Logs.get();
    
    private static Set<String> keyWords = Lang.set("expires", "domain", "path", "secure", "httponly", "samesite");

    protected Map<String, String> map;
    
    protected boolean debug;
    
    protected boolean ignoreNull;

    public Cookie() {
        map = new HashMap<String, String>();
        ignoreNull = true; // 和之前版本行为保持一致
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
        if (debug) {
            log.debug("parse " + str);
        }
        String[] ss = Strings.splitIgnoreBlank(str, ";");
        for (String s : ss) {
            Pair<String> p = Pair.create(Strings.trim(s));
            if (p.getValueString() == null && ignoreNull) {
                continue;
            }
            if (keyWords.contains(p.getName().toLowerCase()))
                continue;
            if ("Max-Age".equalsIgnoreCase(p.getName())) {
                long age = Long.parseLong(p.getValue());
                if (age == 0) {
                    return;
                }
            }
            String val = Strings.sNull(p.getValueString());
            if (debug) {
                log.debugf("add cookie [%s=%s]",  p.getName(), val);
            }
            map.put(p.getName(), val);
        }
    }
    
    @Override
    public String toString() {
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Entry<String, String> en : map.entrySet()) {
            sb.append(en.getKey()).append('=').append(en.getValue()).append("; ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    @Override
    public void beforeConnect(Request request) {
    }
    
    @Override
    public void afterConnect(Request request, HttpURLConnection conn) {
        if (this.map.isEmpty()) {
            return;
        }
        String c = toString();
        if (debug) {
            log.debugf("add Cookie for req [%s]", c);
        }
        if (!Strings.isBlank(c)) {
            conn.addRequestProperty("Cookie", c);
        }
    }
    
    @Override
    public void afterResponse(Request request, HttpURLConnection conn, Response response) {
        Map<String, List<String>> props = conn.getHeaderFields();
        for (Entry<String, List<String>> en : props.entrySet()) {
            if (en.getKey() == null || !"Set-Cookie".equalsIgnoreCase(en.getKey())) {
                continue;
            }
            for (String e : en.getValue()) {
                if (debug) {
                    log.debugf("found Set-Cookie [%s]", e);
                }
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
    
    public void setIgnoreNull(boolean ignoreNull) {
    	this.ignoreNull = ignoreNull;
    }
}
