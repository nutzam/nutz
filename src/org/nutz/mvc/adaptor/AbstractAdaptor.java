package org.nutz.mvc.adaptor;

import java.io.InputStream;
import java.io.Reader;
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

import org.nutz.castor.Castors;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.HttpAdaptor;
import org.nutz.mvc.Scope;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.adaptor.injector.AllAttrInjector;
import org.nutz.mvc.adaptor.injector.AppAttrInjector;
import org.nutz.mvc.adaptor.injector.ArrayInjector;
import org.nutz.mvc.adaptor.injector.CookieInjector;
import org.nutz.mvc.adaptor.injector.HttpInputStreamInjector;
import org.nutz.mvc.adaptor.injector.HttpReaderInjector;
import org.nutz.mvc.adaptor.injector.IocInjector;
import org.nutz.mvc.adaptor.injector.IocObjInjector;
import org.nutz.mvc.adaptor.injector.NameInjector;
import org.nutz.mvc.adaptor.injector.PathArgInjector;
import org.nutz.mvc.adaptor.injector.ReqHeaderInjector;
import org.nutz.mvc.adaptor.injector.RequestAttrInjector;
import org.nutz.mvc.adaptor.injector.RequestInjector;
import org.nutz.mvc.adaptor.injector.ResponseInjector;
import org.nutz.mvc.adaptor.injector.ServletContextInjector;
import org.nutz.mvc.adaptor.injector.SessionAttrInjector;
import org.nutz.mvc.adaptor.injector.SessionInjector;
import org.nutz.mvc.adaptor.injector.ViewModelInjector;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Cookie;
import org.nutz.mvc.annotation.IocObj;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.annotation.ReqHeader;
import org.nutz.mvc.impl.AdaptorErrorContext;

/**
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
public abstract class AbstractAdaptor implements HttpAdaptor {

    private static final Log log = Logs.get();

    public static final String ParamDefailtTag = "//NOT EXIST IN//";

    protected ParamInjector[] injs;

    protected Method method;
    
    protected Class<?>[] argTypes;
    
    protected String[] defaultValues;

    public void init(Method method) {
        this.method = method;
        argTypes = method.getParameterTypes();
        injs = new ParamInjector[argTypes.length];
        defaultValues = new String[argTypes.length];
        Annotation[][] annss = method.getParameterAnnotations();
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < annss.length; i++) {
            Annotation[] anns = annss[i];
            Param param = null;
            Attr attr = null;
            IocObj iocObj = null;
            ReqHeader reqHeader = null;
            Cookie cookie = null;

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
                } else if (anns[x] instanceof ReqHeader) {
                    reqHeader = (ReqHeader) anns[x];
                    break;
                } else if (anns[x] instanceof Cookie) {
                	cookie = (Cookie) anns[x];
                }
            // If has @Attr
            if (null != attr) {
                injs[i] = evalInjectorByAttrScope(attr);
                continue;
            }

            // If has @IocObj
            if (null != iocObj) {
                injs[i] = new IocObjInjector(method.getParameterTypes()[i],
                                             iocObj.value());
                continue;
            }

            if (null != reqHeader) {
                injs[i] = new ReqHeaderInjector(reqHeader.value(), argTypes[i]);
                continue;
            }
            if (null != cookie) {
            	injs[i] = new CookieInjector(cookie.value(), argTypes[i]);
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
            if (param != null) {
            	String tmp = param.df();
            	if (tmp != null && !tmp.equals(ParamDefailtTag))
            		defaultValues[i] = tmp;
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
        // InputStream
        else if (InputStream.class.isAssignableFrom(type)) {
            return new HttpInputStreamInjector();
        }
        // Reader
        else if (Reader.class.isAssignableFrom(type)) {
            return new HttpReaderInjector();
        }
        // ViewModel
        else if (ViewModel.class.isAssignableFrom(type))
            return new ViewModelInjector();
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
     * @return 一个新的参数注入器实例
     */
    protected abstract ParamInjector evalInjectorBy(Type type, Param param);

    public Object[] adapt(ServletContext sc,
                          HttpServletRequest req,
                          HttpServletResponse resp,
                          String[] pathArgs) {
    	
        Object[] args = new Object[argTypes.length];

        if (args.length != injs.length)
            throw new IllegalArgumentException("args.length != injs.length , You get a bug, pls report it!!");

        AdaptorErrorContext errCtx = null;
        // 也许用户有自己的AdaptorErrorContext实现哦
        if (argTypes.length > 0) {
            if (AdaptorErrorContext.class.isAssignableFrom(argTypes[argTypes.length - 1]))
                errCtx = (AdaptorErrorContext) Mirror.me(argTypes[argTypes.length - 1])
                                                     .born(argTypes.length);
        }

        Object obj;
        try {
            obj = getReferObject(sc, req, resp, pathArgs);
        }
        catch (Throwable e) {
            if (errCtx != null) {
                if (log.isInfoEnabled())
                    log.info("Adapter Error catched , but I found AdaptorErrorContext param, so, set it to args, and continue", e);
                errCtx.setAdaptorError(e, this);
                for (int i = 0; i < args.length - 1; i++) {
                	if (args[i] == null) {
                    	if (defaultValues[i] != null) {
                    		args[i] = Castors.me().castTo(defaultValues[i], argTypes[i]);
                    	} else if (argTypes[i].isPrimitive()) {
                    		args[i] = Lang.getPrimitiveDefaultValue(argTypes[i]);
                    	}
                    }
				}
                args[args.length - 1] = errCtx;
                return args;
            }
            throw Lang.wrapThrow(e);
        }

        int len = Math.min(args.length, null == pathArgs ? 0 : pathArgs.length);
        for (int i = 0; i < args.length; i++) {
            Object value = null;
            if (i < len) { // 路径参数
                value = null == pathArgs ? null : pathArgs[i];
            } else { // 普通参数
                value = obj;
            }
            try {
                args[i] = injs[i].get(sc, req, resp, value);
            }
            catch (Throwable e) {
                if (errCtx != null) {
                	log.infof("Adapter Param Error(%s) index=%d", method, i, e);
                    errCtx.setError(i, e, method, value, injs[i]); // 先错误保存起来,全部转好了,再判断是否需要抛出
                } else
                    throw Lang.wrapThrow(e);
            }
            if (args[i] == null) {
            	if (defaultValues[i] != null) {
            		args[i] = Castors.me().castTo(defaultValues[i], argTypes[i]);
            	} else if (argTypes[i].isPrimitive()) {
            		args[i] = Lang.getPrimitiveDefaultValue(argTypes[i]);
            	}
            }
        }

        // 看看是否有任何错误
        if (errCtx == null)
            return args;
        for (Throwable err : errCtx.getErrors()) {
            if (err == null)
                continue;
            int lastParam = argTypes.length - 1;
            if (AdaptorErrorContext.class.isAssignableFrom(argTypes[lastParam])) {
                if (log.isInfoEnabled())
                    log.info("Adapter Param Error catched , but I found AdaptorErrorContext param, so, set it to args, and continue");
                args[lastParam] = errCtx;
                return args;
            }
            // 没有AdaptorErrorContext参数? 那好吧,按之前的方式,抛出异常
            throw Lang.wrapThrow(err);
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
    	if (!Lang.isAndroid) {
    		List<String> names = MethodParamNamesScaner.getParamNames(method);
            if (names != null) {
                String name = names.get(index);
                Class<?> type = method.getParameterTypes()[index];
                if (type.isArray()) {
                    return new ArrayInjector(name,
                                             null,
                                             null,
                                             null,
                                             null,
                                             true);
                }
                return new NameInjector(names.get(index),
                                        null,
                                        method.getParameterTypes()[index],
                                        null, null);
            }
            else if (log.isInfoEnabled())
                log.infof("Complie without debug info? can't deduce param name. fail back to PathArgInjector!! index=%d > %s",
                          index,
                          method);
    	}
        
        return new PathArgInjector(method.getParameterTypes()[index]);
    }
}
