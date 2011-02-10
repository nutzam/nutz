package org.nutz.mvc2.impl;

import javax.servlet.http.HttpServletRequest;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionInvoking;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.UrlMap;
import org.nutz.mvc.invoker.ActionInvokerImpl;
import org.nutz.mvc2.ActionFilterChain;

/**
 * 负责处理请求URI与具体方法的映射关系
 * @author wendal
 *
 */
public class UriMappingActionFilter extends AbstractActionFilter {
	
	private static final Log log = Logs.getLog(UriMappingActionFilter.class);

	@Override
	public void filter(ActionFilterChain chain) throws Throwable {
		HttpServletRequest req = getRequest(chain);
		UrlMap urls = Mvcs.getUrls(getServletContext(chain));
		String path = Mvcs.getRequestPath(req);
		
		if (log.isInfoEnabled())
			log.infof("HttpServletRequest path = %s   , FROM(%s)", path, req.getPathInfo());

		// get Url and invoke it
		ActionInvoking ing = urls.get(path);
		if (null == ing || null == ing.getInvoker()){
			getResponse(chain).sendError(404);
			return;
		}
		ActionInvokerImpl ai = (ActionInvokerImpl) ing.getInvoker();
		chain.put(UrlMap.class.getName(), urls);
		chain.put(ActionFilters.path, path);
		chain.put(ActionFilters.moduleObject, ai.module);
		chain.put(ActionFilters.moduleName, ai.moduleName);
		chain.put(ActionFilters.moduleType, ai.moduleType);
		chain.put(ActionFilters.viewOK, ai.ok);
		chain.put(ActionFilters.viewFail, ai.fail);
		chain.put(ActionFilters.method, ai.method);
		chain.put(ActionFilters.requestEncoding, ai.inputCharset);
		chain.put(ActionFilters.responseEncoding, ai.outputCharset);
		chain.put(ActionFilters.adaptor, ai.adaptor);
		chain.put(ActionFilters.oldActionFilters, ai.filters);
		chain.doChain();
	}
}
