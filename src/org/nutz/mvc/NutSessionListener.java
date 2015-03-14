package org.nutz.mvc;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.nutz.log.Log;
import org.nutz.log.Logs;

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
 * <h4 style=color:red>注意:</h4><br>
 * 如果你的 IocProvider 返回是 Ioc 而不是 Ioc2，那么 这个监听器是没有意义的。因为Nutz.Mvc 不会为 Session 创建
 * IocContext，因此也就不需要注销
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public final class NutSessionListener implements HttpSessionListener {

    private static final Log log = Logs.get();

    /**
     * 如果你在 web.xml 配置了这个监听器，那么我们理解你的意图就是要自动创建 session <br>
     * 否则你就不需要自动创建 session。<br>
     * 在 Nutz 默认的 ModuleProcessor 里，会根据这个变量来决定是否自动创建session的
     */
    public static boolean isSessionScopeEnable = false;

    public NutSessionListener() {
        isSessionScopeEnable = true;
        log.info("NutIoc SessionScope is Enable.");
    }

    public void sessionCreated(HttpSessionEvent se) {}

    public void sessionDestroyed(HttpSessionEvent se) {
        Mvcs.deposeSession(se.getSession());
    }

}
