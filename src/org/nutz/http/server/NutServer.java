package org.nutz.http.server;

import org.nutz.http.server.conn.NutWebConnector;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutServer implements Runnable {
	
	private static final Log log = Logs.get();
	
	protected NutWebContext ctx;
	
	public NutServer(NutWebContext ctx) {
		this.ctx = ctx;
	}

	protected Thread serverThread;
	
	public void start() {
		if (ctx.isRunning())
			return;
		try {
			serverThread = new Thread(this, "NutServer port="+ctx.conf.getAppPort());
			serverThread.start();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public void run() {
		String connClass = ctx.conf.get("nutz.web.connector", "org.nutz.http.server.conn.NIOConnector");
		try {
			ctx.running = true;
			NutWebConnector connector = (NutWebConnector) Class.forName(connClass).getConstructor(NutWebContext.class).newInstance(ctx);
			connector.run();
		} catch (Throwable e) {
			ctx.running = false;
			serverThread = null;
			if (e instanceof ThreadDeath)
				throw (ThreadDeath)e;
			log.error("Exit !!", e);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void shutdown() {
		if (!ctx.isRunning())
			return;
		if (serverThread == null) {
			ctx.running = false;
			return;
		}
		if (serverThread.isAlive()) {
			
			ctx.running = false;
			ctx.es.shutdown();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			if (serverThread.isAlive())
				serverThread.stop();
		}
	}
	
	public static void main(String[] args) {
		String path = Strings.sBlank(Lang.first(args), "nutzweb.properties");
		NutWebConfig conf = new NutWebConfig(path);
		NutWebContext cxt = new NutWebContext();
		cxt.conf = conf;
		NutServer server = new NutServer(cxt);
		log.infof("NutServer start --> port="+conf.getAppPort());
		server.run();
		log.infof("NutServer shutdown --> port="+conf.getAppPort());
	}
}
