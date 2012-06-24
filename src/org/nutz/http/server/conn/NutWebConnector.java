package org.nutz.http.server.conn;

import java.io.IOException;

import org.nutz.http.server.NutServer;

public interface NutWebConnector {
	
	void run(NutServer server) throws IOException ;

}
