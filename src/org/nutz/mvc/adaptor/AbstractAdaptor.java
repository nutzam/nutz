package org.nutz.mvc.adaptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Scope;
import org.nutz.mvc.adaptor.injector.AllAttrInjector;
import org.nutz.mvc.adaptor.injector.AppAttrInjector;
import org.nutz.mvc.adaptor.injector.IocInjector;
import org.nutz.mvc.adaptor.injector.IocObjInjector;
import org.nutz.mvc.adaptor.injector.NameInjector;
import org.nutz.mvc.adaptor.injector.PathArgInjector;
import org.nutz.mvc.adaptor.injector.RequestAttrInjector;
import org.nutz.mvc.adaptor.injector.RequestInjector;
import org.nutz.mvc.adaptor.injector.ResponseInjector;
import org.nutz.mvc.adaptor.injector.ServletContextInjector;
import org.nutz.mvc.adaptor.injector.SessionAttrInjector;
import org.nutz.mvc.adaptor.injector.SessionInjector;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.IocObj;
import org.nutz.mvc.annotation.Param;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
public abstract class AbstractAdaptor implements HttpAdaptor {
	
	private static final Log log = Logs.get();
	
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
			if (null == injs[i]) {
				injs[i] = paramNameInject(method, i);
			}
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
		return evalInjectorBy(type, param);
	}

	/**
	 * 子类实现这个方法根据自己具体的逻辑来生产一个参数注入器
	 * 
	 * @param type
	 *            参数类型
	 * @param param
	 *            参数的注解
	 * @param paramTypes
	 *            参数的范型，无范型的，值为 null
	 * @return 一个新的参数注入器实例
	 */
	protected abstract ParamInjector evalInjectorBy(Type type, Param param);

	public Object[] adapt(	ServletContext sc,
							HttpServletRequest req,
							HttpServletResponse resp,
							String[] pathArgs) {
		Object[] args = new Object[injs.length];
		int len = Math.min(args.length, null == pathArgs ? 0 : pathArgs.length);
		int i = 0;// 确保路径参数不会被覆盖
		// Inject another params
		for (; i < len; i++) {
			args[i] = injs[i].get(sc, req, resp, null == pathArgs ? null : pathArgs[i]);
		}
		Class<?>[] argTypes = method.getParameterTypes();
		Object obj = getReferObject(sc, req, resp, pathArgs);
		for (; i < injs.length; i++) {
			args[i] = injs[i].get(sc, req, resp, obj);
			if (args[i] == null && argTypes[i].isPrimitive()) {
				args[i] = Lang.getPrimitiveDefaultValue(argTypes[i]);
			}
		}
		return args;
	}

	protected Object getReferObject(ServletContext sc,
									HttpServletRequest req,
									HttpServletResponse resp,
									String[] pathArgs) {
		return null;
	}
	
	/**
	 * 这是最后的大招了,查一下形参的名字,作为@Param("形参名")进行处理
	 */
	protected ParamInjector paramNameInject(Method method, int index) {
		List<String> names = MethodParamNamesScaner.getParamNames(method);
		if (names != null)
			return new NameInjector(names.get(index), method.getParameterTypes()[index], null);
		else if (log.isInfoEnabled())
			log.info("Complie without debug info? can't deduce param name. fail back to PathArgInjector!! index="+index);
		return new PathArgInjector(method.getParameterTypes()[index]);
	}
}
