package org.nutz.mvc.impl;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nutz.json.Json;
import org.nutz.lang.Each;
import org.nutz.lang.Invoking;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

/**
 * JSON-RPC 简单封装
 * <p/>
 * <code>@At<p/>@Ok("json")<p/>public NutMap jsonrpc(Reader r){<p/>return JsonRPC.invoke(this);<p/>}</code>
 *
 * @author wendal
 * @since 1.r.56
 *
 */
@SuppressWarnings("unchecked")
public class JsonRPC {

    public static final int ParseError = -32700;
    public static final int InvalidRequest = -32600;
    public static final int MethodNotFound = -32601;
    public static final int InvalidParams = -32602;
    public static final int InternalError = -32603;
    public static final int ServerError = -32000;
    public static final String Version = "2.0";

    public static NutMap invoke(final Object obj, Reader r) {
        final NutMap resp = new NutMap();
        resp.setv("jsonrpc", Version);
        Object req;
        try {
            req = Json.fromJson(r);
        } catch (Exception e) {
            return resp.setv("error", error(ParseError, "Parse error", E(e)));
        }
        if (req == null) {
            return resp.setv("error", error(InvalidRequest, "Invalid Request", "json is null"));
        }

        if (req instanceof Iterable) {// rpc批量调用
            final List<NutMap> results = new ArrayList<NutMap>();
            Lang.each(req, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    if (ele instanceof Map) {
                        results.add(_invoke(obj, new NutMap((Map<String, Object>) ele)));
                    } else {
                        NutMap _resp = new NutMap();
                        _resp.setv("jsonrpc", Version).setv("error", error(InvalidRequest, "Invalid Request", "not map or list"));
                        results.add(_resp);
                    }
                }
            });
            return resp.setv("result", results);
        } else if (req instanceof Map) { // 单一调用
            return _invoke(obj, new NutMap((Map<String, Object>) req));
        } else { // 传的是什么鸟,拒绝
            return resp.setv("error", error(InvalidRequest, "Invalid Request", "not map or list"));
        }
    }

    public static NutMap _invoke(Object obj, NutMap req) {
        NutMap resp = new NutMap();
        String version = req.getString("jsonrpc", Version);
        String id = req.getString("id");
        resp.setv("id", id);
        resp.setv("jsonrpc", version);
        String method = req.getString("method");
        if (Strings.isBlank(method)) {
            return resp.setv("error", error(InvalidRequest, "Invalid Request", "method name is blank"));
        }
        List<Object> params = req.getList("params", Object.class, Collections.EMPTY_LIST);
        Invoking ing;
        try {
            ing = Mirror.me(obj).getInvoking(method, params.toArray());
        } catch (Exception e) {
            return resp.setv("error", error(MethodNotFound, "Method not found", E(e)));
        }
        try {
            return resp.setv("result", ing.invoke(obj));
        } catch (Exception e) {
            return resp.setv("error", error(ServerError, e.getMessage(), E(e)));
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            return resp.setv("error", error(InternalError, e.getMessage(), E(e)));
        }
    }

    protected static NutMap error(int code, String message, Object data) {
        return new NutMap().setv("code", code).setv("message", message).setv("data", data);
    }

    protected static String E(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        return sw.toString();
    }
}
