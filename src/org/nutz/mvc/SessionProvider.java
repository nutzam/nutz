package org.nutz.mvc;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Session提供者,采用过滤器形式,获取一个用户自行定义的HttpServletRequest,并在NutzMVC的作用域内,替代原有的HttpServletRequest对象
 * @author wendal
 *
 */
public interface SessionProvider {

	/**
	 * 过滤一个请求,返回被改造过的req对象. 一般情况下就是继承HttpServletRequestWrapper然后override其getSession方法. 注意,不可返回null!!
	 */
    HttpServletRequest filter(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext);
    
    /**
     * web容器销毁(stop/shutdown)时调用的通知方法
     */
    void notifyStop();
}
