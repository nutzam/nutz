package org.nutz.web;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 一个 Web 服务的启动器
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebLauncher {

	private static final Log log = Logs.get();

	/**
	 * 启动的主函数，接受一个参数，为 web 服务器的配置文件路径。如果没有这个参数，默认在 classpath 下寻找
	 * "web.properties" 文件。
	 * <p>
	 * 这个文件遵循 Nutz 多行属性文件规范，同时必须具备如下的键:
	 * <ul>
	 * <li>"app-root" - 应用的根路径，比如 "~/workspace/git/danoo/strato/domain/ROOT"
	 * <li>"app-port" - 应用监听的端口，比如 8080
	 * <li>"app-rs" - 应用静态资源的地址前缀，比如 "http://localhost/strato"，或者 "/rs" 等
	 * <li>"app-classpath" - 应用的类路径，可多行
	 * <li>"admin-port" - 应用的管理端口，比如 8081
	 * </ul>
	 * 这个文件的例子，请参看源码 conf 目录下的 web.properties
	 * 
	 * @param args
	 *            接受一个参数作为 web 服务器的配置文件路径
	 */
	public static void main(String[] args) {
		String path = Strings.sBlank(Lang.first(args), Webs.CONF_PATH);

		log.infof("launch by '%s'", path);

		final WebServer server = new WebServer(new WebConfig(path));

		server.run();

		log.info("Server is down!");
	}

}
