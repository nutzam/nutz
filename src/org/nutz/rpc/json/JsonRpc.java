package org.nutz.rpc.json;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nutz.castor.Castors;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Each;
import org.nutz.lang.Invoking;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;

/**
 * JSON-RPC 简单封装
 * <p/>
 * <code>@At<p/>@Ok("json")<p/>public NutMap jsonrpc(Reader r){<p/>return JsonRpc.invoke(r, this, null);<p/>}</code>
 *
 * @author wendal
 * @since 1.r.56
 *
 */
@SuppressWarnings("unchecked")
public class JsonRpc {

    public static final int ParseError = -32700;
    public static final int InvalidRequest = -32600;
    public static final int MethodNotFound = -32601;
    public static final int InvalidParams = -32602;
    public static final int InternalError = -32603;
    public static final int ServerError = -32000;
    public static final String Version = "2.0";
    public static final String JSONRPC = "jsonrpc";
    public static final String ERROR = "error";
    public static final String RESULT = "result";
    public static final String CODE = "code";
    public static final String MESSAGE = "message";
    public static final String METHOD = "method";
    public static final String ID = "id";
    public static final String PARAMS = "params";
    public static final String NAMESPACE = "namespace";
    public static final String DATA = "data";
    
    public static NutMap invoke(Reader r, final Object dft) {
    	return invoke(r, dft, null);
    }

    /**
     * 服务端
     */
    public static NutMap invoke(Reader r, final Object dft, final NutMap namespaces) {
        final NutMap resp = new NutMap();
        resp.setv(JSONRPC, Version);
        Object req;
        try {
            req = Json.fromJson(r);
        } catch (Exception e) {
            return resp.setv(ERROR, error(ParseError, "Parse error", E(e)));
        }
        if (req == null) {
            return resp.setv(ERROR, error(InvalidRequest, "Invalid Request", "json is null"));
        }

        if (req instanceof Iterable) {// rpc批量调用
            final List<NutMap> results = new ArrayList<NutMap>();
            Lang.each(req, new Each<Object>() {
                public void invoke(int index, Object ele, int length) {
                    if (ele instanceof Map) {
                        results.add(JsonRpc.invoke(new NutMap((Map<String, Object>) ele), dft, namespaces));
                    } else {
                        NutMap _resp = new NutMap();
                        _resp.setv(JSONRPC, Version).setv(ERROR, error(InvalidRequest, "Invalid Request", "not map or list"));
                        results.add(_resp);
                    }
                }
            });
            return resp.setv(RESULT, results);
        } else if (req instanceof Map) { // 单一调用
            return invoke(new NutMap((Map<String, Object>) req), dft, namespaces);
        } else { // 传的是什么鸟,拒绝
            return resp.setv(ERROR, error(InvalidRequest, "Invalid Request", "not map or list"));
        }
    }

    public static NutMap invoke(NutMap req, Object dft, NutMap namespaces) {
        NutMap resp = new NutMap();
        String version = req.getString(JSONRPC, Version);
        String id = req.getString(ID);
        resp.setv(ID, id);
        resp.setv(JSONRPC, version);
        String method = req.getString(METHOD);
        if (Strings.isBlank(method)) {
            return resp.setv(ERROR, error(InvalidRequest, "Invalid Request", "method name is blank"));
        }
        String namespace = req.getString(NAMESPACE);
        Object obj = dft;
        if (namespace != null) {
        	if (namespaces == null) {
        		return resp.setv(ERROR, error(InvalidRequest, "Invalid Request", "namespaces not set at server "));
        	}
        	obj = namespaces.get(namespace);
        }
        if (obj == null) {
        	return resp.setv(ERROR, error(InvalidRequest, "Invalid Request", "no such obj"));
        }
        List<Object> params = req.getList(PARAMS, Object.class, Collections.EMPTY_LIST);
        Invoking ing;
        try {
            ing = Mirror.me(obj).getInvoking(method, params.toArray());
        } catch (Exception e) {
            return resp.setv(ERROR, error(MethodNotFound, "Method not found", E(e)));
        }
        try {
            return resp.setv(RESULT, ing.invoke(obj));
        } catch (Exception e) {
            return resp.setv(ERROR, error(ServerError, e.getMessage(), E(e)));
        } catch (Throwable e) {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            return resp.setv(ERROR, error(InternalError, e.getMessage(), E(e)));
        }
    }

    protected static NutMap error(int code, String message, Object data) {
        return new NutMap().setv(CODE, code).setv(MESSAGE, message).setv(DATA, data);
    }

    protected static String E(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter writer = new PrintWriter(sw);
        e.printStackTrace(writer);
        return sw.toString();
    }

    /**
     * 客户端. 用于生成一个代理接口的实例,透明访问json-rpc服务
     * @param klass 需要代理的接口
     * @param endpoint jsonrpc URL入口
     * @param namespace 命名空间,非json-rpc标准,扩展用,不需要就传null
     * @param timeout 超时设置,若永不超时,设置为-1
     * @return 代理实例
     */
    public static <T> T mapper(Class<T> klass, final String endpoint, final String namespace, final int timeout) {
        return (T)Proxy.newProxyInstance(klass.getClassLoader(), new Class<?>[]{klass}, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                NutMap jreq = new NutMap();
                jreq.setv(JSONRPC, Version).setv(ID, R.UU32()).setv(METHOD, method.getName());
                if (!Strings.isBlank(namespace)) {
                    jreq.put(NAMESPACE, namespace);
                }
                jreq.setv(PARAMS, args);
                Request req = Request.create(endpoint, org.nutz.http.Request.METHOD.POST);
                req.setData(Json.toJson(jreq));
                Response resp = Sender.create(req).setTimeout(timeout).send();
                if (resp.isOK()) {
                    if (method.getReturnType() == Void.class)
                        return null;
                    NutMap re = Json.fromJson(NutMap.class, resp.getReader());
                    Object error = re.get(ERROR);
                    if (error != null) {
                    	throw new JsonRpcException(Json.toJson(error));
                    }
                    Object result = re.get(RESULT);
                    return Castors.me().castTo(result, method.getReturnType());
                }
                throw new JsonRpcException("resp code="+resp.getStatus());
            }
        });
    }
}
