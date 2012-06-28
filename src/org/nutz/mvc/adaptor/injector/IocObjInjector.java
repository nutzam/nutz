package org.nutz.mvc.adaptor.injector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.ParamInjector;

/**
 * 通过注解 '@IocObj' 可以启用这个参数注入器。
 * <p>
 * 如果系统挂载了 Ioc 容器，则会为该参数赋值，否则则抛出一个运行时异常
 * <p>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.mvc.annotation.IocObj
 */
public class IocObjInjector implements ParamInjector {

    private String objName;

    private Class<?> objType;

    public IocObjInjector(Class<?> objType, String objName) {
        this.objType = objType;
        this.objName = objName;
    }

    public Object get(ServletContext sc, HttpServletRequest req, HttpServletResponse resp, Object refer) {
        Ioc ioc = Mvcs.getIoc();
        if (null == ioc)
            throw new RuntimeException("You need define @IocBy in main module!!!");
        if (Strings.isBlank(objName))
            return ioc.get(objType);
        return ioc.get(objType, objName);
    }

}
