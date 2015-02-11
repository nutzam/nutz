package org.nutz.mvc.adaptor.injector;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.castor.Castors;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.mvc.adaptor.ParamInjector;

public class NameInjector implements ParamInjector {

    protected String name;
    protected DateFormat dfmt;

    protected Class<?> klass;
    protected Type type;
    protected Type[] paramTypes;
    protected String defaultValue;

    public NameInjector(String name,
                        String datefmt,
                        Type type,
                        Type[] paramTypes,
                        String defaultValue) {
        this.klass = Mirror.me(type).getType();
        if (null == name)
            throw Lang.makeThrow("Can not accept null as name, type '%s'",
                                 klass.getName());
        this.name = name;
        if (Strings.isBlank(datefmt) || !Mirror.me(klass).isDateTimeLike()) {
            dfmt = null;
        } else {
            dfmt = new SimpleDateFormat(datefmt);
        }
        this.type = type;
        this.paramTypes = paramTypes;
        this.defaultValue = defaultValue;
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
    public Object get(ServletContext sc,
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
                if (value == null) { // TODO 临时解决JsonAdaptor丢URL参数的问题
                    return fromReqParam(req);
                }
                // 如果 value 是集合，并且有范型参数，需要预先将集合内的对象都转换一遍
                // Issue #32
                if ((value instanceof Collection<?>)
                    && null != paramTypes
                    && paramTypes.length > 0) {
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
                return Castors.me().castTo(value, klass);
            }
            // 普通对象，直接转
            else {
                return Castors.me().castTo(refer, klass);
            }
        /*
         * 直接从 http params 里取
         */
        return fromReqParam(req);
    }

    public Object fromReqParam(HttpServletRequest req) {
        String[] params = req.getParameterValues(name);
        // 不为 null，那么必然要转换成日期
        if (null != dfmt && params != null && params.length > 0) {
            Object o = Times.parseq(dfmt, params[0]);
            return Castors.me().castTo(o, klass);
        }
        if (params == null || params.length == 0) {
        	if (defaultValue != null)
        		params = new String[]{defaultValue};
        }
        // 默认用转换器转换
        return Castors.me().castTo(params, klass);
    }
}
