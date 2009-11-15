package org.nutz.mvc;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 如果你的应用，在 Session 中保存了一些需要注销的对象，比如你在 Ioc 容器中将一个 DataSource 对象的范围设成
 * "session"，那么请启用本的监听器，它会在一个 session 注销时，关闭 DataSource
 * <p>
 * 启用的方法是在 web.xml 中，添加下面的代码：
 * 
 * <pre>
 * &lt;listener&gt;
 * &lt;listerner-class&gt;org.nutz.mvc.NutSessionListener&lt;/listerner-class&gt;
 * &lt;/listener&gt;
 * </pre>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class NutSessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent se) {}

	public void sessionDestroyed(HttpSessionEvent se) {
		Mvcs.deposeSession(se.getSession());
	}

}
