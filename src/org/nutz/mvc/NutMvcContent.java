package org.nutz.mvc;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.Nutz;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.init.Inits;
import org.nutz.mvc.init.NutConfig;

/**
 * Nutz Mvc 处理器
 * @author juqkai (juqkai@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class NutMvcContent {

	private static final Log log = Logs.getLog(NutMvcContent.class);

	/**
	 * Nutz.Mvc 的URL映射表
	 */
	private UrlMap urls;
	
	private NutConfig config;

	/**
	 * Nutz Mvc 初始化
	 */
	public void init(NutConfig config) throws ServletException {
		if (log.isInfoEnabled()) {
			URL me = NutMvcContent.class.getResource(
						NutMvcContent.class.getName().replace('.', '/')
									+ ".class");
			log.infof("Nutz Version : %s in %s", Nutz.version(), me);
		}
		this.config = config;
		Loading ing = Inits.init(config, false);
		if (null != ing)
			urls = ing.getUrls();
	}

	public void destroy() {
		if (config.getMainModule() != null)
			Inits.destroy(config);
	}

	/**
	 * 处理函数
	 * @return 
	 * <ul>
	 * <li>true
	 * 		处理成功
	 * <li>false
	 * 		处理失败
	 */
	public boolean handle(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		if (null == urls) {
			throw new RuntimeException("!!URLMap is NULL !This servlet can't handle request!!!");
		}

		Mvcs.updateRequestAttributes(req);

		String path = Mvcs.getRequestPath(req);
		
		if (log.isInfoEnabled())
			log.infof("HttpServletRequest path = %s   , FROM(%s)", path, req.getPathInfo());

		// get Url and invoke it
		ActionInvoking ing = urls.get(path);
		if (null == ing || null == ing.getInvoker()){
			return false;
		}
		ing.invoke(config.getServletContext(), req, resp);
		return true;
	}
}
