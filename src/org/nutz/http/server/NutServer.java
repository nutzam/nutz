package org.nutz.http.server;

import org.nutz.http.server.conn.NutWebConnector;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.web.WebConfig;

public class NutServer implements Runnable {
	
	private static final Log log = Logs.get();
	
	protected WebConfig cfg;
	
	public NutServer(WebConfig cfg) {
		this.cfg = cfg;
	}

	protected Thread serverThread;
	
	protected boolean running;
	
	public void start() {
		if (running)
			return;
		try {
			serverThread = new Thread(this, "NutServer port="+cfg.getAppPort());
			serverThread.start();
			serverThread.interrupt();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
		running = true;
	}
	
	public void run() {
		NutWebContext ctx = new NutWebContext();
		ctx.port = cfg.getAppPort();
		ctx.root = cfg.getAppRoot();
		String connClass = cfg.get("nutz.web.connector", "org.nutz.http.server.conn.NIOConnector");
		try {
			running = true;
			NutWebConnector connector = (NutWebConnector) Class.forName(connClass).getConstructor(NutWebContext.class).newInstance(ctx);
			connector.run();
		} catch (Throwable e) {
			running = false;
			serverThread = null;
			if (e instanceof ThreadDeath)
				throw (ThreadDeath)e;
			log.error("Exit !!", e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void shutdown() {
		if (!running)
			return;
		if (serverThread == null) {
			running = false;
			return;
		}
		if (serverThread.isAlive()) {
			running = false;
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			if (serverThread.isAlive())
				serverThread.stop();
		}
	}
	
	public static void main(String[] args) {
		WebConfig cfg = new WebConfig(null);
		NutServer server = new NutServer(cfg);
		server.run();
	}

}
