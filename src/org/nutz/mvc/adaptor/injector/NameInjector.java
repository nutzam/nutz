package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.ParamInjector;

public class NameInjector implements ParamInjector {

    protected String name;
    protected Class<?> type;
    protected Type[] paramTypes;

    public NameInjector(String name, Class<?> type, Type[] paramTypes) {
        if (null == name)
            throw Lang.makeThrow("Can not accept null as name, type '%s'", type.getName());
        this.name = name;
        this.type = type;
        this.paramTypes = paramTypes;
    }

    /**
     * @param req
     *            请求对象
     * @param resp
     *            响应对象
     * @param refer
     *            这个参考字段，如果有值，表示是路径参数的值，那么它比 request 里的参数优先
     * @return 注入值
     */
    @SuppressWarnings("unchecked")
    public Object get(    ServletContext sc,
                        HttpServletRequest req,
                        HttpServletResponse resp,
                        Object refer) {
        /*
         * 有 refer 就不能从 http params 里取了
         */
        if (null != refer)
            // Map 对象，详细分析一下
            if (refer instanceof Map<?, ?>) {
                Object value = ((Map<?, ?>) refer).get(name);
                if (value == null) { //TODO 临时解决JsonAdaptor丢URL参数的问题
                    return fromReqParam(req);
                }
                // 如果 value 是集合，并且有范型参数，需要预先将集合内的对象都转换一遍
                // Issue #32
                if ((value instanceof Collection<?>) && null != paramTypes && paramTypes.length > 0) {
                    try {
                        Collection<?> col = ((Collection<?>) value);
                        Collection<Object> nw = col.getClass().newInstance();
                        Class<?> eleType = Lang.getTypeClass(paramTypes[0]);
                        for (Object ele : col) {
                            Object obj = Castors.me().castTo(ele, eleType);
                            nw.add(obj);
                        }
                        value = nw;
                    }
                    catch (Exception e) {
                        throw Lang.wrapThrow(e);
                    }
                }
                return Castors.me().castTo(value, type);
            }
            // 普通对象，直接转
            else {
                return Castors.me().castTo(refer, type);
            }
        /*
         * 直接从 http params 里取
         */
        return fromReqParam(req);
    }

    public Object fromReqParam(HttpServletRequest req) {
        String[] params = req.getParameterValues(name);
        return Castors.me().castTo(params, type);
    }
}
