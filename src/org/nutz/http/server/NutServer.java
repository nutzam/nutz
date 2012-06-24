package org.nutz.http.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.http.impl.Https;
import org.nutz.http.impl.Mimes;
import org.nutz.http.impl.NutHttpAction;
import org.nutz.http.impl.NutHttpReq;
import org.nutz.http.impl.NutHttpResp;
import org.nutz.http.server.conn.NutWebConnector;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NutServer implements Runnable {
	
	private static final Log log = Logs.get();
	
	protected Context webContext = Lang.context();
	
	protected boolean running;
	
	public final ExecutorService es = Executors.newFixedThreadPool(1024);
	
	public final List<NutHttpAction> actions = new ArrayList<NutHttpAction>();
	
	protected NutWebConfig conf;
	
	public NutServer(NutWebConfig conf) {
		this.conf = conf;
	}

	protected Thread serverThread;
	
	public void start() {
		if (running)
			return;
		try {
			serverThread = new Thread(this, "NutServer port="+conf.getAppPort());
			serverThread.start();
		} catch (Throwable e) {
			throw Lang.wrapThrow(e);
		}
	}
	
	public void run() {
		String connClass = conf.get("nutz.web.connector", "org.nutz.http.server.conn.NIOConnector");
		try {
			running = true;
			NutWebConnector connector = (NutWebConnector) Class.forName(connClass).newInstance();
			connector.run(this);
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
			es.shutdown();

			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {}
			if (serverThread.isAlive())
				serverThread.stop();
		}
	}
	
	public void workFor(final Socket socket, final byte[] head, final byte[] preRead) {
		if (!running) {
			try {
				socket.close();
			} catch (IOException e1) {}
			return;
		}
			
		es.execute(new Runnable() {
			
			public void run() {
				try {
					NutHttpReq req = Https.makeHttpReq(webContext, socket, head, preRead);
					NutHttpAction action = findAction(req);
					if (action == null)
						action = defaultHttpAction;
					log.debug("Work for req URI="+req.requestURI());
					req.analysisParameters(); //TODO 推迟到第一次获取参数的时候
					action.exec(req, req.resp());
					log.debug("Done for req URI="+req.requestURI());
				} catch (Throwable e) {
					log.warn(e.getMessage(), e);
				} finally {
					try {
						socket.close();
					} catch (IOException e1) {}
				}
			}
		});
	}
	
	public NutHttpAction findAction(NutHttpReq req) {
		for (NutHttpAction action : actions) {
			if (action.canWork(req))
				return action;
		}
		return null;
	}
	
	protected NutHttpAction defaultHttpAction = new NutHttpAction() {
		
		public void exec(NutHttpReq req, NutHttpResp resp) {
			try {
				File f = new File(conf.getAppRoot() + req.requestURI());
				if (f.exists() && f.isDirectory()) {
					f = new File(conf.getAppRoot() + req.requestURI() + "/index.html");
				}
				log.debugf("File=%s, exist=%s", f.getPath(), f.exists());
				if (f.exists() && f.isFile()) {
					resp.setContentLength((int)f.length());
					resp.setContentType(Mimes.guess(Files.getSuffixName(f)));
					resp.headers().setDate("Last-Modify", f.lastModified());
					resp.sendRespHeaders();
					Streams.write(resp.getOutputStream(), new FileInputStream(f));
				} else {
					log.debug("File not found , send 404");
					resp.sendError(404, "File Not Found", null);
				}
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
		
		public boolean canWork(NutHttpReq req) {
			return true;
		}
	};
	
	public boolean isRunning() {
		return running;
	}
	
	public NutWebConfig conf() {
		return conf;
	}
	
	public static void main(String[] args) {
		String path = Strings.sBlank(Lang.first(args), "nutzweb.properties");
		NutWebConfig conf = new NutWebConfig(path);
		NutServer server = new NutServer(conf);
		log.infof("NutServer start --> port="+conf.getAppPort());
		server.run();
		log.infof("NutServer shutdown --> port="+conf.getAppPort());
	}
}
