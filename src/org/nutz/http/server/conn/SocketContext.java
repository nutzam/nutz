package org.nutz.http.server.conn;

import java.net.Socket;

public class SocketContext {

	public Socket socket;
	public int readPos;
	public byte[] buf;
}
