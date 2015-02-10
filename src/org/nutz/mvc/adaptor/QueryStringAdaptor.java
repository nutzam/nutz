package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;

/**
 * 普通的 HTTP 请求当调用 req.getParameterValues 时，会读请求体。 这个适配器将只从 QueryString
 * 读取请求参数，这样对用 HTML5 的上传流，可以不破坏其结构
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QueryStringAdaptor extends PairAdaptor {

    protected ParamInjector getNameInjector(String pm, String datefmt,
    		Type type, Type[] paramTypes, String defaultValue) {
    	return new QueryStringNameInjector(pm, datefmt, type, paramTypes, defaultValue);
    }

}
