package org.nutz.http.server.conn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.nutz.http.impl.Https;
import org.nutz.http.impl.NutHttpReq;
import org.nutz.http.impl.NutWebContext;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NIOConnector {
	
	private static final Log log = Logs.get();

	protected int port = 8080;
	
	protected NutWebContext ctx = new NutWebContext();
	
	protected boolean running = true;
	
	public void run() {
		//启动监听线程
		new Thread() {
			public void run() {
				setName("NIOConnector port="+port);
				try {
					_run();
				} catch (IOException e) {
					acceptConn = false;
					running = false;
					log.error("Start fail!!", e);
					throw Lang.wrapThrow(e);
				}
			}
		}.start();
	}
	
	protected boolean acceptConn = true;
	
	public void _run() throws IOException {
		Selector selector = Selector.open();
		
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(port);
		serverSocketChannel.socket().bind(address);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		log.debug("Listening ...");
		while (acceptConn) {
			try {
				int s = selector.select();
				if (s < 1) {
					continue;
				}
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) iterator.next();
					iterator.remove();
					if(!selectionKey.isValid())
						continue;
					if (selectionKey.isAcceptable()) {
						ServerSocket serverSocket = serverSocketChannel.socket();
			            serverSocket.setReceiveBufferSize(8192);
			            serverSocket.setReuseAddress(true);

						if (log.isDebugEnabled()) {
							log.debug("NIO --> isAcceptable=true");
						}
						boolean hasSocket = true;
			            do {
			            	SocketChannel socketChannel = serverSocketChannel.accept();
			                if (null != socketChannel) {
			                	socketChannel.configureBlocking(false);  
				            	socketChannel.socket();
				            	socketChannel.register(selector, SelectionKey.OP_READ);
			                } else {
			                  hasSocket = false;
			                }
			             } while (hasSocket);
					} else if (selectionKey.isReadable()) {
						log.debug("selectionKey isReadable=true");
						int pos = handleConn(selectionKey);
						if (pos > 0) { //看来请求头拿到了!!
							log.debug("Req head readed!!");
							selectionKey.cancel();
							selectionKey.channel().configureBlocking(true);
							SocketContext socketContext = (SocketContext) selectionKey.attachment();
							Socket socket = socketContext.socket;
							
							int readPos = socketContext.getInt("read_pos");
							byte[] buf = (byte[])socketContext.get("read_buf");
							byte[] head = new byte[pos];
							System.arraycopy(buf, 0, head, 0, pos);
							
							byte[] preRead = null;
							if (readPos > pos) {
								preRead = new byte[readPos - pos];
								System.arraycopy(buf, pos, preRead, 0, preRead.length);
							}
							
							NutHttpReq req = Https.makeHttpReq(ctx, socket, head, preRead);
							ctx.workFor(req);
						}
					} else {
						log.debug("Unuse key, drop it -->" + selectionKey);
					}
				}
			} catch (Exception e) {
				log.info("Dead socket", e);
			}
		}
		serverSocketChannel.close();
	}
	
	protected int handleConn(SelectionKey selectionKey) throws IOException {
		SocketContext socketContext = (SocketContext) selectionKey.attachment();
		if (socketContext == null) { //New conn
			socketContext = new SocketContext();
			selectionKey.attach(socketContext);
			byte[] buf = new byte[4192];
			ByteBuffer dst = ByteBuffer.allocate(4096);
			socketContext.set("read_buf", buf);
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			Socket socket = socketChannel.socket();
			socketContext.socket = socket;
			int len = socketChannel.read(dst);
			if (len == -1) {
				socketChannel.socket().close();
				throw new IOException("conn is CLOSED!" + socketChannel);
			}
			dst.flip();
			dst.get(buf, 0, dst.remaining());
			socketContext.set("read_pos", len);
		} else {
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			byte[] buf = (byte[])socketContext.get("read_buf");
			int readPos = socketContext.getInt("read_pos");
			ByteBuffer dst = ByteBuffer.allocate(4096 - readPos);
			int len = socketChannel.read(dst);
			if (len == -1) {
				socketChannel.socket().close();
				throw new IOException("conn is CLOSED!");
			}
			dst.flip();
			byte[] _buf = new byte[dst.remaining()];
			dst.get(_buf, 0, dst.remaining());
			System.arraycopy(_buf, 0, buf, readPos, _buf.length);
			socketContext.set("read_pos", len + readPos);
		}
		
		//尝试解码Http头
		byte[] buf = (byte[])socketContext.get("read_buf");
		for (int i = 0; i < buf.length; i++) {
			if (buf[i] == '\r') {
				if (buf.length - i > 3) {
					if (buf[i+1] == '\n' && buf[i+2] == '\r' && buf[i+3] == '\n')
						return i+3;
				}
			}
		}
		int readPos = socketContext.getInt("read_pos");
		if (readPos >= 4192)
			throw Lang.makeThrow("Head too large!");
		
		return -1;
	}
}

