package org.nutz.mvc;

import java.util.List;

import javax.servlet.ServletContext;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.Context;
import org.nutz.mvc.config.AtMap;

/**
 * 这个接口是一个抽象封装
 * <p>
 * 如果是通过 Servlet 方式加载的 Nutz.Mvc， 只需要根据 ServletConfig 来实现一下这个接口 同理， Filter
 * 方式，甚至不是标准的 JSP/Servlet 容器，只要实现了这个接口，都可以 正常的调用 Loading 接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface NutConfig {

    /**
     * @return 当前应用的 IOC 容器实例
     */
    Ioc getIoc();

    /**
     * @return 当前应用的根路径
     */
    String getAppRoot();

    /**
     * @return 当前应用的名称
     */
    String getAppName();

    /**
     * 获取配置的参数
     * 
     * @param name
     *            参数名
     * @return 参数值
     */
    String getInitParameter(String name);

    /**
     * 获取配置参数的名称列表
     * 
     * @return 配置参数的名称列表
     */
    List<String> getInitParameterNames();

    /**
     * 获取上下文环境中的属性对象
     * 
     * @param name
     *            - 属性名
     * 
     * @return 值
     */
    Object getAttribute(String name);

    /**
     * 获取上下文环境中属性名称的列表
     * 
     * @return 属性名称列表
     */
    List<String> getAttributeNames();

    /**
     * 获取上下文环境中的属性对象，并自动转成指定类型
     * 
     * @param <T>
     *            类型
     * @param type
     *            类型
     * @param name
     *            属性名
     * @return 值
     */
    <T> T getAttributeAs(Class<T> type, String name);

    /**
     * 在上下文环境中设置属性对象
     * 
     * @param name
     *            属性名
     * @param obj
     *            属性值
     */
    void setAttribute(String name, Object obj);

    /**
     * 在上下文环境中设置属性对象，如果值为 null，则忽略
     * 
     * @param name
     *            属性名
     * @param obj
     *            属性值
     */
    void setAttributeIgnoreNull(String name, Object obj);

    /**
     * 获取配置的主模块，一般的说是存放在 initParameter 集合下的 "modules" 属性 值为一个 class 的全名
     * 
     * @return 配置的主模块，null - 如果没有定义这个参数
     */
    Class<?> getMainModule();

    /**
     * 在你的模块中通过 '@At' 声明的入口函数，可以存储在 AtMap 中，这个函数提供一个 AtMap 的实例
     */
    AtMap getAtMap();

    /**
     * 根据 MainModule 中的 '@LoadingBy' 得到一个加载逻辑的实现类
     * 
     * @return 加载逻辑
     */
    Loading createLoading();

    /**
     * 如果在非 JSP/SERVLET 容器内，这个函数不保证返回正确的结果
     * 
     * @return 当前应用的上下文对象
     */
    ServletContext getServletContext();

    /**
     * 加载时上下文包括环境变量，以及 "app.root" 等信息
     * 
     * @return 加载时上下文
     */
    Context getLoadingContext();

    void setSessionProvider(SessionProvider provider);
    
    SessionProvider getSessionProvider();
}
