package org.nutz.http.server.conn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.nutz.http.impl.NutWebContext;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class NIOConnector {
	
	private static final Log log = Logs.get();

	protected int port;
	
	protected NutWebContext ctx;
	
	protected Selector handleSelector;
	
	protected boolean running = true;
	
	public void run() {
		//准备好
		try {
			handleSelector = Selector.open();
			new Thread() {
				public void run() {
					while (true) {
						try {
							int s = handleSelector.select();
							if (s < 1)
								continue;
							Iterator<SelectionKey> iterator = handleSelector.selectedKeys().iterator();
							while (iterator.hasNext()) {
								SelectionKey selectionKey = (SelectionKey) iterator.next();
								iterator.remove();
								if(!selectionKey.isValid())
									continue;
								if (selectionKey.isReadable())
									handleConn(selectionKey);
							}
						} catch (IOException e) {
							log.info("IO not OK", e);
						}
					}
				};
			}.start();
		} catch (IOException e1) {
			throw Lang.wrapThrow(e1);
		}
		
		//启动监听线程
		new Thread() {
			public void run() {
				setName("NIOConnector port="+port);
				try {
					_run();
				} catch (IOException e) {
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
		ServerSocketChannel ssChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(port);
		ssChannel.socket().bind(address);
		ssChannel.configureBlocking(false);
		ssChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		while (acceptConn) {
			try {
				int s = selector.select();
				if (s < 1) {
					continue;
				}
				acceptConn(selector);
			} catch (IOException e) {
				log.info("Dead socket", e);
			}
		}
	}
	
	protected void acceptConn(Selector selector) throws IOException{
		Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
		while (iterator.hasNext()) {
			SelectionKey selectionKey = (SelectionKey) iterator.next();
			iterator.remove();
			if(!selectionKey.isValid())
				continue;
			if (selectionKey.isAcceptable()) {
				if (log.isDebugEnabled()) {
					log.debug("NIO --> isAcceptable=true");
				}
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
				SocketChannel socketChannel = serverSocketChannel.accept();
				socketChannel.configureBlocking(false);
				socketChannel.register(handleSelector, SelectionKey.OP_READ);
			}
		}
	}
	
	protected void handleConn(SelectionKey selectionKey) throws IOException {
		SocketContext socketContext = (SocketContext) selectionKey.attachment();
		if (socketContext == null) { //New conn
			socketContext = new SocketContext();
			ByteBuffer dst = ByteBuffer.allocate(4096);
			socketContext.set("read_buf", dst);
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			Socket socket = socketChannel.socket();
			socketContext.set("socket", socket);
			int len = socketChannel.read(dst);
			if (len == -1)
				throw new IOException("conn is CLOSED!");
			socketContext.set("read_pos", len);
		} else {
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			ByteBuffer dst = socketContext.getAs(ByteBuffer.class, "read_buf");
			int readPos = socketContext.getInt("read_pos");
			int len = (int) socketChannel.read(new ByteBuffer[]{dst}, readPos, 4096 - readPos);
			if (len == -1)
				throw new IOException("conn is CLOSED!");
			socketContext.set("read_pos", len + readPos);
		}
		
		//尝试解码Http头
		ByteBuffer _buf = socketContext.getAs(ByteBuffer.class, "read_buf").asReadOnlyBuffer();
		_buf.flip();
	}
}


