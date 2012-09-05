package org.nutz.mvc;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.Ioc;
import org.nutz.lang.util.SimpleContext;

/**
 * Action执行的上下文
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 *
 */
public class ActionContext extends SimpleContext {

    private static final String PATH = "nutz.mvc.path";
    private static final String PATH_ARGS = "nutz.mvc.pathArgs";

    private static final String REQUEST = HttpServletRequest.class.getName();
    private static final String RESPONSE = HttpServletResponse.class.getName();
    private static final String SERVLET_CONTEXT = ServletContext.class.getName();

    private static final String MODULE = "nutz.mvc.module";
    private static final String METHOD = "nutz.mvc.method";
    private static final String METHOD_ARGS = "nutz.mvc.method.args";
    private static final String METHOD_RETURN = "nutz.mvc.method.return";
    
    private static final String ERROR = "nutz.mvc.error";

    /**
     * 获取全局的Ioc对象
     * @return 如果定义了IocBy注解,则肯定返回非空对象
     */
    public Ioc getIoc() {
        return Mvcs.getIoc();
    }

    /**
     * 获取异常对象
     */
    public Throwable getError() {
        return this.getAs(Throwable.class, ERROR);
    }

    /**
     * 设置异常对象,一般由ActionChain捕捉到异常后调用
     * @param error 异常对象
     * @return 当前上下文,即被调用者本身
     */
    public ActionContext setError(Throwable error) {
        this.set(ERROR, error);
        return this;
    }

    /**
     * 获取当前请求的path,经过去后缀处理
     * @return 当前请求的path,经过去后缀处理
     */
    public String getPath() {
        return this.getString(PATH);
    }

    /**
     * 设置当前请求的path,经过去后缀处理
     * @param ph 请求的path,,经过去后缀处理
     * @return 当前上下文,即被调用者本身
     */
    public ActionContext setPath(String ph) {
        this.set(PATH, ph);
        return this;
    }

    /**
     * 获取路径参数
     * @return 路径参数
     */
    @SuppressWarnings("unchecked")
    public List<String> getPathArgs() {
        return this.getAs(List.class, PATH_ARGS);
    }

    public ActionContext setPathArgs(List<String> args) {
        this.set(PATH_ARGS, args);
        return this;
    }

    /**
     * 获取这个Action对应的Method
     */
    public Method getMethod() {
        return this.getAs(Method.class, METHOD);
    }

    /**
     * 设置这个Action对应的Method
     * @param m 这个Action对应的Method
     * @return 当前上下文,即被调用者本身
     */
    public ActionContext setMethod(Method m) {
        this.set(METHOD, m);
        return this;
    }

    /**
     * 获取将要执行Method的对象
     * @return 执行对象,即模块类的实例
     */
    public Object getModule() {
        return this.get(MODULE);
    }

    public ActionContext setModule(Object obj) {
        this.set(MODULE, obj);
        return this;
    }

    /**
     * 获取将要执行Method的参数
     * @return method的参数
     */
    public Object[] getMethodArgs() {
        return this.getAs(Object[].class, METHOD_ARGS);
    }

    public ActionContext setMethodArgs(Object[] args) {
        this.set(METHOD_ARGS, args);
        return this;
    }

    /**
     * 获取method返回值
     */
    public Object getMethodReturn() {
        return this.get(METHOD_RETURN);
    }

    public ActionContext setMethodReturn(Object re) {
        this.set(METHOD_RETURN, re);
        return this;
    }

    /**
     * 获取请求的HttpServletRequest
     * @return 请求的HttpServletRequest
     */
    public HttpServletRequest getRequest() {
        return this.getAs(HttpServletRequest.class, REQUEST);
    }

    public ActionContext setRequest(HttpServletRequest req) {
        this.set(REQUEST, req);
        return this;
    }

    /**
     * 获取请求的HttpServletResponse
     * @return 请求的HttpServletResponse
     */
    public HttpServletResponse getResponse() {
        return this.getAs(HttpServletResponse.class, RESPONSE);
    }

    public ActionContext setResponse(HttpServletResponse resp) {
        this.set(RESPONSE, resp);
        return this;
    }

    /**
     * 获取ServletContext
     * @return ServletContext
     */
    public ServletContext getServletContext() {
        return this.getAs(ServletContext.class, SERVLET_CONTEXT);
    }

    public ActionContext setServletContext(ServletContext sc) {
        this.set(SERVLET_CONTEXT, sc);
        return this;
    }
    
    public String toString() {
        return getInnerMap().toString();
    }
}
