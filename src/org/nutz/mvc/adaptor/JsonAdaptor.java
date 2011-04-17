package org.nutz.mvc.adaptor;

import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.mvc.adaptor.injector.JsonInjector;
import org.nutz.mvc.annotation.Param;

/**
 * 假设，整个输入输入流，是一个 JSON 字符串
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class JsonAdaptor extends AbstractAdaptor {

	public Object getReferObject(	ServletContext sc,
							HttpServletRequest request,
							HttpServletResponse response, String[] pathArgs) {
		// Read all as String
		try {
			return Streams.readAndClose(Streams.utf8r(request.getInputStream()));
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Override
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		return new JsonInjector(type, null == param ? null : param.value());
	}
	protected ParamInjector evalInjector(Type type, Param param){
		return new JsonInjector(type, null == param ? null : param.value());
	}

}
