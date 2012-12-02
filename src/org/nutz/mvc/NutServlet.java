package org.nutz.mvc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.util.Context;
import org.nutz.mvc.config.ServletNutConfig;

/**
 * 挂接到 JSP/Servlet 容器的入口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 */
@SuppressWarnings("serial")
public class NutServlet extends HttpServlet {

    protected ActionHandler handler;
    
    private String selfName;
    
    private SessionProvider sp;

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Mvcs.setServletContext(servletConfig.getServletContext());
        selfName = servletConfig.getServletName();
        Mvcs.set(selfName, null, null);
        NutConfig config = new ServletNutConfig(servletConfig);
        Mvcs.setNutConfig(config);
        handler = new ActionHandler(config);
        sp = config.getSessionProvider();
    }

    public void destroy() {
        Mvcs.resetALL();
        Mvcs.set(selfName, null, null);
        if(handler != null)
            handler.depose();
        Mvcs.setServletContext(null);
        Mvcs.close();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String preName = Mvcs.getName();
        Context preContext = Mvcs.resetALL();
        try {
            if (sp != null)
                req = sp.filter(req, resp, getServletContext());
            Mvcs.set(selfName, req, resp);
            if (!handler.handle(req, resp))
                resp.sendError(404);
        } finally {
            Mvcs.resetALL();
            //仅当forward/incule时,才需要恢复之前设置
            if (null != (req.getAttribute("javax.servlet.forward.request_uri"))) {
                if (preName != null)
                    Mvcs.set(preName, req, resp);
                if (preContext != null)
                    Mvcs.ctx.reqThreadLocal.set(preContext);
            }
        }
    }
}
