package org.nutz.http.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.http.impl.HttpObject;
import org.nutz.http.impl.Mimes;
import org.nutz.http.impl.NutHttpAction;
import org.nutz.http.impl.NutHttpReq;
import org.nutz.http.impl.NutHttpResp;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;


public class NutWebContext extends HttpObject {
	
	private static final Log log = Logs.get();
	
	public final ExecutorService es = Executors.newFixedThreadPool(1024);
	
	public final List<NutHttpAction> actions = new ArrayList<NutHttpAction>();
	
	protected NutWebConfig conf;
	public NutWebConfig conf() {
		return conf;
	}
	
	protected boolean running;
	
	public void setRunning(boolean running) {
		this.running = running;
	}
	public boolean isRunning() {
		return running;
	}
	
	public void workFor(final NutHttpReq req) {
		if (!running) {
			try {
				req.socket().close();
			} catch (IOException e1) {}
			return;
		}
			
		es.execute(new Runnable() {
			
			public void run() {
				NutHttpAction action = findAction(req);
				if (action == null)
					action = defaultHttpAction;
				try {
					log.debug("Work for req URI="+req.requestURI());
					req.analysisParameters(); //TODO 推迟到第一次获取参数的时候
					action.exec(req, req.resp());
					log.debug("Done for req URI="+req.requestURI());
				} catch (Throwable e) {
					log.warn(e.getMessage(), e);
				} finally {
					try {
						req.socket().close();
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
				if (f.exists() && f.isFile()) {
					resp.setContentLength((int)f.length());
					resp.setContentType(Mimes.guess(Files.getSuffixName(f)));
					resp.headers().setDate("Last-Modify", f.lastModified());
					resp.sendRespHeaders();
					Streams.write(resp.getOutputStream(), new FileInputStream(f));
				} else {
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
}
