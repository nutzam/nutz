package org.nutz.http.server;

import org.nutz.http.impl.NutWebContext;
import org.nutz.http.server.conn.NIOConnector;

public class NutServer {

	public static void main(String[] args) {
		NutWebContext ctx = new NutWebContext();
		ctx.port = 8080;
		new NIOConnector(ctx).run();
	}

}
