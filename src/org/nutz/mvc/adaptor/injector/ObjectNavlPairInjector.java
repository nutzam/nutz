package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.mvc.adaptor.ParamInjector;

/**
 * 对象导航注入器 默认情况下只有使用 @Param("::") 的情况下才调用这个注入器
 * <p/>
 * 毕竟它在接收到请求时进行注入,会有一定的性能损伤
 * 
 * @author juqkai(juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * 
 */
public class ObjectNavlPairInjector implements ParamInjector {
	protected Mirror<?> mirror;
	private String prefix;

	public ObjectNavlPairInjector(String prefix, Class<?> type) {
		prefix = Strings.isBlank(prefix) ? "" : Strings.trim(prefix);
		this.prefix = prefix;
		this.mirror = Mirror.me(type);
	}

	public Object get(	ServletContext sc,
						HttpServletRequest req,
						HttpServletResponse resp,
						Object refer) {
		ObjcetNaviNode no = new ObjcetNaviNode();
		String pre = "";
		if ("".equals(prefix))
			pre = "node.";
		for (Object name : req.getParameterMap().keySet()) {
			String na = (String) name;
			if (na.startsWith(prefix)) {
				no.put(pre + na, req.getParameter(na));
			}
		}
		return no.inject(mirror);
	}

}
