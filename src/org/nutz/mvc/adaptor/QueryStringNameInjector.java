package org.nutz.mvc.adaptor;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.nutz.castor.Castors;
import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.meta.Pair;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.injector.NameInjector;

public class QueryStringNameInjector extends NameInjector {

    public QueryStringNameInjector(String name,
                                   String datefmt,
                                   Type type,
                                   Type[] paramTypes,
                                   String defaultValue) {
        super(name, datefmt, type, paramTypes, defaultValue);
    }

    @Override
    public Object fromReqParam(HttpServletRequest req) {
        String params;
        // 如果是整个 QueryString ...
        if ("?".equals(name)) {
            params = req.getQueryString();
        }
        // 试图分析 QueryString 的名值对
        else {
            // 得到 QueryString Map
            NutMap qsMap = (NutMap) req.getAttribute("_nutz_qs_map");
            if (null == qsMap) {
                qsMap = new NutMap();
                // 分析
                String qs = req.getQueryString();
                String[] ss = Strings.splitIgnoreBlank(qs, "[&]");
                for (String s : ss) {
                    Pair<String> p = Pair.create(s);
                    String val = p.getValue();
                    if (Strings.isBlank(val)) {
                        qsMap.put(p.getName(), true);
                    } else {
                        try {
                            val = URLDecoder.decode(val, Encoding.UTF8);
                        }
                        catch (UnsupportedEncodingException e) {
                            throw Lang.wrapThrow(e);
                        }
                        qsMap.put(p.getName(), val);
                    }
                }
                // 保存
                req.setAttribute("_nutz_qs_map", qsMap);
            }
            // 得到某个参数的值
            params = qsMap.getString(name);
        }
        // 不为 null，那么必然要转换成日期
        if (null != dfmt && params != null) {
            Object o = Times.parseq(dfmt, params);
            return Castors.me().castTo(o, klass);
        }
        // 默认用转换器转换
        return Castors.me().castTo(params, klass);
    }

}
