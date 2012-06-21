package org.nutz.web;

import java.io.File;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.nutz.http.Http;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.socket.SocketAction;
import org.nutz.lang.socket.SocketContext;
import org.nutz.lang.socket.Sockets;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 这个类将调用 Jetty 的类启动一个 HTTP 服务，并提供关闭这个服务的 Socket 端口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebServer {

	private static final Log log = Logs.get();

	private WebConfig dc;

	private Server server;

	public WebServer(WebConfig config) {
		this.dc = config;
	}

	void run() {
		try {
			server = new Server(dc.getAppPort());
			// 设置应用上下文
			File root = Files.findFile(dc.getAppRoot());
			String warUrlString = root.toURI().toURL().toExternalForm();
			WebAppContext appContext = new WebAppContext(warUrlString, "/");
			appContext.setExtraClasspath(dc.getAppClasspath());
			server.setHandler(appContext);

			// 启动
			server.start();

			// 自省一下,判断自己是否能否正常访问
			Response resp = Http.get("http://127.0.0.1:" + dc.getAppPort());
			if (resp == null || resp.getStatus() >= 400) {
				log.error("Self-Testing fail !!Server start fail?!!");
				server.stop();
				return;
			}

			if (log.isInfoEnabled())
				log.info("Server is up!");

			// 管理
			if (log.isInfoEnabled())
				log.infof("Create admin port at %d", dc.getAdminPort());
			Sockets.localListenOne(dc.getAdminPort(), "stop", new SocketAction() {
				public void run(SocketContext context) {
					if (null != server)
						try {
							server.stop();
						}
						catch (Exception e4stop) {
							if (log.isErrorEnabled())
								log.error("Fail to stop!", e4stop);
						}
					Sockets.close();
				}
			});

		}
		catch (Throwable e) {
			if (log.isWarnEnabled())
				log.warn("Unknow error", e);
		}

	}

	@Override
	protected void finalize() throws Throwable {
		if (null != server)
			try {
				server.stop();
			}
			catch (Throwable e) {
				if (log.isErrorEnabled())
					log.error("Fail to stop!", e);
				throw e;
			}
		super.finalize();
	}

}
