package org.nutz.mvc;

import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.Nutz;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.init.InitException;
import org.nutz.mvc.init.Inits;
import org.nutz.mvc.init.NutConfig;

/**
 * Nutz Mvc 处理器
 * @author juqkai (juqkai@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 */
public class NutMvc {
	private static final Log log = Logs.getLog(NutServlet.class);

	/**
	 * Nutz.Mvc 的参数映射表
	 */
	private UrlMap urls;
	private NutConfig config;
	private static NutMvc nutMvc;
	private NutMvc(){}
	public static NutMvc make(){
		if(nutMvc == null){
			nutMvc = new NutMvc();
		}
		return nutMvc;
	}

	/**
	 * Nutz Mvc 初始化
	 */
	public void init(NutConfig config) throws ServletException {
		if (log.isInfoEnabled()) {
			URL me = Thread
					.currentThread()
					.getContextClassLoader()
					.getResource(
							NutServlet.class.getName().replace('.', '/')
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
	 * @throws InitException
	 * 		urls初始化失败,不能进行Mvc的行为
	 */
	public boolean handle(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		return handle(null, req, resp);
	}
	/**
	 * 处理函数
	 * @param path
	 * 		需要处理的请求地址
	 * <ul>
	 * <li>空 
	 * 		从 req 中获取请求地址
	 * <li>非空
	 * 		需要进行处理的请求地址
	 * </ul>
	 * @return 
	 * <ul>
	 * <li>true
	 * 		处理成功
	 * <li>false
	 * 		处理失败
	 * @throws InitException
	 * 		urls初始化失败,不能进行Mvc的行为
	 */
	public boolean handle(String path, HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		if (null == urls) {
			if (log.isErrorEnabled())
				log.error("!!!This servlet is destroyed!!! Noting to do!!!");
			//TODO 这里异常类型可能要斟酌一下.
			throw new InitException("");
		}
		if(path == null || "".equals(path)){
			path = Mvcs.getRequestPath(req);
		} else {
			path = Mvcs.getRequestPathObject(path).getPath();
		}

		Mvcs.updateRequestAttributes(req);

		if (log.isInfoEnabled())
			log.info("HttpServletRequest path = " + path);

		// get Url and invoke it
		ActionInvoking ing = urls.get(path);
		if (null == ing || null == ing.getInvoker()){
			return false;
		}
		ing.invoke(config.getServletContext(), req, resp);
		return true;
	}
}
