package org.nutz.mvc.adaptor;

import java.lang.annotation.Annotation;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.injector.*;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.IocObj;
import org.nutz.mvc.annotation.Param;
/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public abstract class AbstractAdaptor implements HttpAdaptor {

	protected ParamInjector[] injs;
	
	protected Method method;

	public void init(Method method) {
		this.method = method;
		Class<?>[] argTypes = method.getParameterTypes();
		injs = new ParamInjector[argTypes.length];
		Annotation[][] annss = method.getParameterAnnotations();
		Type[] types = method.getGenericParameterTypes();
		for (int i = 0; i < annss.length; i++) {
			Annotation[] anns = annss[i];
			Param param = null;
			Attr attr = null;
			IocObj iocObj = null;

			// find @Param & @Attr & @IocObj in current annotations
			for (int x = 0; x < anns.length; x++)
				if (anns[x] instanceof Param) {
					param = (Param) anns[x];
					break;
				} else if (anns[x] instanceof Attr) {
					attr = (Attr) anns[x];
					break;
				} else if (anns[x] instanceof IocObj) {
					iocObj = (IocObj) anns[x];
					break;
				}
			// If has @Attr
			if (null != attr) {
				injs[i] = evalInjectorByAttrScope(attr);
				continue;
			}

			// If has @IocObj
			if (null != iocObj) {
				injs[i] = new IocObjInjector(method.getParameterTypes()[i], iocObj.value());
				continue;
			}

			// And eval as default suport types
			injs[i] = evalInjectorByParamType(argTypes[i]);
			if (null != injs[i])
				continue;
			// Eval by sub-classes
			injs[i] = evalInjector(types[i], param);
			// 子类也不能确定，如何适配这个参数，那么做一个标记，如果
			// 这个参数被 ParamInjector 适配到，就会抛错。
			// 这个设计是因为了 "路径参数"
			if (null == injs[i])
				injs[i] = new ErrorInjector(method, i);
		}
	}



	private static ParamInjector evalInjectorByAttrScope(Attr attr) {
		if (attr.scope() == Scope.APP)
			return new AppAttrInjector(attr.value());
		if (attr.scope() == Scope.SESSION)
			return new SessionAttrInjector(attr.value());
		if (attr.scope() == Scope.REQUEST)
			return new RequestAttrInjector(attr.value());
		return new AllAttrInjector(attr.value());
	}

	private static ParamInjector evalInjectorByParamType(Class<?> type) {
		// Request
		if (ServletRequest.class.isAssignableFrom(type)) {
			return new RequestInjector();
		}
		// Response
		else if (ServletResponse.class.isAssignableFrom(type)) {
			return new ResponseInjector();
		}
		// Session
		else if (HttpSession.class.isAssignableFrom(type)) {
			return new SessionInjector();
		}
		// ServletContext
		else if (ServletContext.class.isAssignableFrom(type)) {
			return new ServletContextInjector();
		}
		// Ioc
		else if (Ioc.class.isAssignableFrom(type)) {
			return new IocInjector();
		}
		return null;
	}

	protected ParamInjector evalInjector(Type type, Param param) {
		Class<?> clazz;
		if(type instanceof ParameterizedType){
			ParameterizedType pt = (ParameterizedType) type;
			clazz = (Class<?>) pt.getRawType();
		} else {
			clazz = (Class<?>) type;
		}
		return evalInjector(clazz, param);
	}
	
	/**
	 * 子类应该覆盖这个方法或者evalInjector(Type,Param)方法.
	 */
	protected ParamInjector evalInjector(Class<?> type, Param param) {
		return null;
	}

	public Object[] adapt(	ServletContext sc,
							HttpServletRequest req,
							HttpServletResponse resp,
							String[] pathArgs) {
		Object[] args = new Object[injs.length];
		int len = Math.min(args.length, null == pathArgs ? 0 : pathArgs.length);
		int i = 0;//确保路径参数不会被覆盖
		// Inject another params
		for (; i < len; i++) {
			args[i] = injs[i].get(sc, req, resp, null == pathArgs ? null : pathArgs[i]);
		}
		Class<?>[] argTypes = method.getParameterTypes();
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(sc, req, resp, null);
			if(args[i] == null && argTypes[i].isPrimitive()) {
				args[i] = getPrimitiveDefaultValue(argTypes[i]);
			}
		}
		return args;
	}

	protected int fillPathArgs(	HttpServletRequest req,
								HttpServletResponse resp,
								String[] pathArgs,
								Object[] args) {
		int i = 0;
		// Loop path args
		if (null != pathArgs) {
			int len = Math.min(args.length, pathArgs.length);
			for (; i < len; i++)
				args[i] = injs[i].get(null, req, resp, pathArgs[i]);
		}
		return i;
	}

	protected Object getPrimitiveDefaultValue(Class<?> pClass){
		if(int.class.equals(pClass))
			return Integer.valueOf(0);
		if(long.class.equals(pClass))
			return Long.valueOf(0);
		if(short.class.equals(pClass))
			return Short.valueOf((short) 0);
		if(float.class.equals(pClass))
			return Float.valueOf(0f);
		if(double.class.equals(pClass))
			return Double.valueOf(0);
		if(byte.class.equals(pClass))
			return Byte.valueOf((byte) 0);
		if(char.class.equals(pClass))
			return Character.valueOf((char)0);
		if(boolean.class.equals(pClass))
			return Boolean.FALSE;
		return null;
	}
}
