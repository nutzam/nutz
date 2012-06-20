package org.nutz.http.server;

import org.nutz.http.server.conn.NIOConnector;

public class NutServer {

	public static void main(String[] args) {
		new NIOConnector().run();
	}

}
