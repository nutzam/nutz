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

import org.nutz.http.server.NutServer;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 将客户端的请求,转换为一个NutHttpReq对象,然后交给容器继续处理 ~~ 其他就不管啦!
 * @author wendal
 *
 */
public class NIOConnector implements NutWebConnector {
	
	private static final Log log = Logs.get();
	
	public void run(NutServer server) throws IOException {
		//打开Selector
		Selector selector = Selector.open();
		
		//开启ServerSocketChannel
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
		InetSocketAddress address = new InetSocketAddress(server.conf().getAppPort());
		serverSocketChannel.socket().bind(address);
		serverSocketChannel.configureBlocking(false);
		serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		//开始监听
		log.debug("Listening ...");
		while (server.isRunning()) {
			try {
				int s = selector.select(3000);//等3s就可以循环一次
				if (s < 1) { //如果为0,那就是啥都没有咯
					continue;
				}
				Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					SelectionKey selectionKey = (SelectionKey) iterator.next();
					iterator.remove();
					if(!selectionKey.isValid()) //必须确保是有效的
						continue;
					if (selectionKey.isAcceptable()) { //新连接!!
						ServerSocket serverSocket = serverSocketChannel.socket();
			            serverSocket.setReceiveBufferSize(8192);
			            serverSocket.setReuseAddress(true);

						if (log.isDebugEnabled())
							log.debug("NIO --> isAcceptable=true");
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
					} else if (selectionKey.isReadable()) { //可读,那么,继续之前的读取吧!!
						log.debug("selectionKey isReadable=true");
						int pos = handleConn(selectionKey); //根据获取的请求头,判断是否需要继续读数据
						if (pos > 0) { //看来请求头拿到了!!
							log.debug("Req head readed!!");
							selectionKey.cancel(); //把它无效掉,这样才能恢复为阻塞式IO
							selectionKey.channel().configureBlocking(true);
							SocketContext socketContext = (SocketContext) selectionKey.attachment();
							Socket socket = socketContext.socket;
							
							int readPos = socketContext.readPos;
							byte[] buf = socketContext.buf;
							byte[] head = new byte[pos];
							System.arraycopy(buf, 0, head, 0, pos); //头部是必须有的!
							
							byte[] preRead = null;
							if (readPos > pos) { //被超读的部分,需要并入Socket.Inputstream呢
								preRead = new byte[readPos - pos];
								System.arraycopy(buf, pos, preRead, 0, preRead.length);
							}
							
							server.workFor(socket, head, preRead);//交给容器处理了, 我不管了!!
						}
					} else { //按理说,不应该有其他key的出现
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
		if (socketContext == null) { //新连接,没上下文,那就新建一个咯
			socketContext = new SocketContext();
			selectionKey.attach(socketContext);
			byte[] buf = new byte[4192]; //TODO 改成可配置的
			ByteBuffer dst = ByteBuffer.allocate(4096);
			socketContext.buf = buf;
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			Socket socket = socketChannel.socket();
			socketContext.socket = socket;
			int len = socketChannel.read(dst);
			if (len == -1) {
				socket.close();
				throw new IOException("conn is CLOSED!" + socketChannel);
			}
			dst.flip();
			dst.get(buf, 0, dst.remaining());
			socketContext.readPos = len;
		} else {
			SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
			byte[] buf = socketContext.buf;
			int readPos = socketContext.readPos;
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
			socketContext.readPos = len + readPos;
		}
		
		//尝试解码Http头
		int readPos = socketContext.readPos;
		byte[] buf = socketContext.buf;
		for (int i = 0; i < readPos; i++) { //TODO 增加一个标记值,这样就不必重复判断了
			if (buf[i] == '\r') { // 正确的HTTP请求,头部都是由\r\n\r\n结束的!!
				if (buf.length - i > 3) {
					if (buf[i+1] == '\n' && buf[i+2] == '\r' && buf[i+3] == '\n')
						return i+3;
				}
			}
		}
		if (readPos >= 4192) //TODO 做成可配置的
			throw Lang.makeThrow("Http Head too large!");
		
		return -1;
	}
}

