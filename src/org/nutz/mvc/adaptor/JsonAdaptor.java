package org.nutz.mvc.adaptor;

import java.io.InputStreamReader;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Encoding;
import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.injector.JsonInjector;
import org.nutz.mvc.annotation.Param;

/**
 * 假设，整个输入输入流，是一个 JSON 字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonAdaptor extends AbstractAdaptor {

	public Object[] adapt(	ServletContext sc,
							HttpServletRequest request,
							HttpServletResponse response, String[] pathArgs) {
		// Read all as String
		String str;
		try {
			str = Lang.readAll(new InputStreamReader(request.getInputStream(), Encoding.CHARSET_UTF8));
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
		//Map<String, Object> map = Lang.map(str);
		// Try to make the args
		Object[] args = new Object[injs.length];
		int i = fillPathArgs(request, response, pathArgs, args);
		// Inject another params
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(sc, request, response, str);
		}
		return args;
	}

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		return new JsonInjector(type, null == param ? null : param.value());
	}
	protected ParamInjector evalInjector(Type type, Param param){
		return new JsonInjector(type, null == param ? null : param.value());
	}

}
