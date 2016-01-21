package org.nutz.mvc;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.config.FilterNutConfig;

/**
 * 同 JSP/Serlvet 容器的挂接点
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @author juqkai(juqkai@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class NutFilter implements Filter {
	
	protected static Log log;

    protected ActionHandler handler;

    protected static final String IGNORE = "^.+\\.(jsp|png|gif|jpg|js|css|jspx|jpeg|swf|ico)$";

    protected Pattern ignorePtn;

    protected String selfName;

    protected SessionProvider sp;

    private NutFilter2 proxyFilter;//代理老版本的Filter
    
    /**
     * 需要排除的路径前缀
     */
    protected Pattern exclusionsPrefix;
    /**
     * 需要排除的后缀名
     */
    protected Pattern exclusionsSuffix;
    /**
     * 需要排除的固定路径
     */
    protected Set<String> exclusionPaths;
    
    protected ServletContext sc;
    
    public void init(FilterConfig conf) throws ServletException {
    	try {
    		_init(conf);
    	} finally {
    		Mvcs.set(null, null, null);
            Mvcs.ctx().removeReqCtx();
    	}
    }

    public void _init(FilterConfig conf) throws ServletException {
        log = Logs.getLog(getClass());
    	sc = conf.getServletContext();
    	Mvcs.setServletContext(sc);
    	if ("true".equals(Strings.sNull(conf.getInitParameter("skip-mode"), "false").toLowerCase())) {
    		log.infof("NutFilter[%s] run as skip-mode", conf.getFilterName());
    		proxyFilter = new NutFilter2();
    		return;
    	}
    	log.infof("NutFilter[%s] starting ...", conf.getFilterName());
        this.selfName = conf.getFilterName();
        Mvcs.set(selfName, null, null);

        FilterNutConfig config = new FilterNutConfig(conf);
        Mvcs.setNutConfig(config);
        handler = new ActionHandler(config);
        String regx = Strings.sNull(config.getInitParameter("ignore"), IGNORE);
        if (!"null".equalsIgnoreCase(regx)) {
            ignorePtn = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        }
        String exclusions = config.getInitParameter("exclusions");
        if (exclusions != null) {
        	String[] tmps = Strings.splitIgnoreBlank(exclusions);
        	Set<String> prefix = new HashSet<String>();
        	Set<String> suffix = new HashSet<String>();
        	Set<String> paths = new HashSet<String>();
        	for (String tmp : tmps) {
        		tmp = tmp.trim().intern();
        		if (tmp.length() > 1) {
        			if (tmp.startsWith("*")) {
        			    suffix.add(tmp.substring(1));
    					continue;
    				} else if (tmp.endsWith("*")) {
    					prefix.add(tmp.substring(0, tmp.length() - 1));
    					continue;
    				}
        		}
				paths.add(tmp);
			}
        	if (prefix.size() > 0) {
        		exclusionsPrefix = Pattern.compile("^("+Lang.concat("|", prefix)+")", Pattern.CASE_INSENSITIVE);
        		log.info("exclusionsPrefix  = " + exclusionsPrefix);
        	}
        	if (suffix.size() > 0) {
        		exclusionsSuffix = Pattern.compile("("+Lang.concat("|", suffix)+")$", Pattern.CASE_INSENSITIVE);
        		log.info("exclusionsSuffix = " + exclusionsSuffix);
        	}
        	if (paths.size() > 0) {
        		exclusionPaths = paths;
        		log.info("exclusionsPath   = " + exclusionPaths);
        	}
        }
        sp = config.getSessionProvider();
    }

    public void destroy() {
    	if (proxyFilter != null)
    		return;
        Mvcs.resetALL();
        Mvcs.set(selfName, null, null);
        if (handler != null)
            handler.depose();
        Mvcs.close();
        Mvcs.setServletContext(null);
        Mvcs.set(null, null, null);
        Mvcs.ctx().removeReqCtx();
    }
    
    /**
     * 过滤请求. 过滤顺序(ignorePtn,exclusionsSuffix,exclusionsPrefix,exclusionPaths)
     * @param matchUrl
     * @return
     * @throws IOException
     * @throws ServletException
     */
    protected boolean isExclusion(String matchUrl) throws IOException, ServletException {
    	if (ignorePtn != null && ignorePtn.matcher(matchUrl).find()) {
    		return true;
    	}
    	if (exclusionsSuffix != null) {
    		if (exclusionsSuffix.matcher(matchUrl).find()) {
	    		return true;
    		}
    	}
    	if (exclusionsPrefix != null) {
    		if (exclusionsPrefix.matcher(matchUrl).find()) {
	    		return true;
    		}
    	}
    	if (exclusionPaths != null && exclusionPaths.contains(matchUrl)) {
    		return true;
    	}
    	return false;
    }

    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain)
            throws IOException, ServletException {
    	ServletContext prCtx = Mvcs.getServletContext();
    	Mvcs.setServletContext(sc);
    	if (proxyFilter != null) {
    		proxyFilter.doFilter(req, resp, chain);
    		return;
    	}
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String matchUrl = request.getServletPath() + Strings.sBlank(request.getPathInfo());
        
        String markKey = "nutz_ctx_mark";
        Integer mark = (Integer) req.getAttribute(markKey);
    	if (mark != null) {
    		req.setAttribute(markKey, mark+1);
    	} else {
    		req.setAttribute(markKey, 0);
    	}
    	
        String preName = Mvcs.getName();
        Context preContext = Mvcs.resetALL();
        try {
            if (sp != null)
                request = sp.filter(request,
                                response,
                                Mvcs.getServletContext());
            Mvcs.set(this.selfName, request, response);
            if (!isExclusion(matchUrl)) {
                if (handler.handle(request, response))
                    return;
            }
            nextChain(request, response, chain);
        }
        finally {
            //仅当forward/incule时,才需要恢复之前设置
            if (mark != null) {
                Mvcs.ctx().reqCtx(preContext);
            	Mvcs.setServletContext(prCtx);
                Mvcs.set(preName, request, response);
                if (mark == 0) {
                	req.removeAttribute(markKey);
                } else {
                	req.setAttribute(markKey, mark - 1);
                }
            } else {
            	Mvcs.set(null, null, null);
                Mvcs.ctx().removeReqCtx();
                Mvcs.setServletContext(null);
            }
        }
    }
    
    protected void nextChain(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws IOException, ServletException {
    	// 更新 Request 必要的属性
        Mvcs.updateRequestAttributes((HttpServletRequest) req);
        // 本过滤器没有找到入口函数，继续其他的过滤器
        chain.doFilter(req, resp);
	}
}
