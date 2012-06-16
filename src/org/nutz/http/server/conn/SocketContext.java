package org.nutz.http.server.conn;

import java.io.ByteArrayOutputStream;
import java.net.Socket;

import org.nutz.lang.util.SimpleContext;

public class SocketContext extends SimpleContext {

	protected Socket socket;
	protected ByteArrayOutputStream bos;
}
