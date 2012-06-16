package org.nutz.http.server.conn;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;

import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 最小化的Http服务器
 */
public class MiniHttpServer {

	private static final Log log = Logs.get();
	private ExecutorService es = Executors.newCachedThreadPool();
	
	int port;
	String root;
	public MiniHttpServer(int port, String root) {
		this.port = port;
		this.root = root;
	}
	
	public void run() throws IOException {
		ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
		while (true) {
			try {
				new HttpReqSession(serverSocket.accept());
			} catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
		}
	}
	
	class HttpReqSession extends Thread {
		
		private Socket socket;
		public HttpReqSession(Socket socket) {
			this.socket = socket;
			es.execute(this);
		}
		
		@Override
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				if (in == null) return;
				byte[] buf = new byte[4092];
				int firstLen = in.read(buf);
				if (firstLen < 1)
					return; //Emtry ?!
				boolean dataEnd = false;
				if (firstLen < 4092) {
					dataEnd = true;
				}
				HttpReq req = decodeHeader(buf, firstLen);
				if (req == null) {
					log.info("BAD Req!");
					return;
				}
			} 
			catch (Exception e) {
				log.warn(e.getMessage(), e);
			}
			finally {
				try {
					socket.close();
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
			}
		}
		
		public HttpReq decodeHeader(byte[] buf, int firstLen) throws Exception {
			ByteArrayInputStream bis = new ByteArrayInputStream(buf, 0, firstLen);
			InputStreamReader reader = new InputStreamReader(bis, "ISO-8859-1");
			BufferedReader br = new BufferedReader(reader);
			String statusLine = br.readLine();
			StringTokenizer tokenizer = new StringTokenizer(statusLine);
			if (tokenizer.countTokens() != 3) {
				log.debug("Not a req?! --> " + statusLine);
				return null;
			}
			HttpReq req = new HttpReq();
			req.method = tokenizer.nextToken();
			req.uri = tokenizer.nextToken();
			if (req.uri.contains("?")) {
				req.queryString = req.uri.substring(req.uri.indexOf('?'));
				req.uri = req.uri.substring(0, req.uri.indexOf('?'));
			}
			String emtryLine = br.readLine();
			if (emtryLine != null) {
				log.debug("Error headers?! " + emtryLine);
				return null;
			}
			String header = br.readLine();
			while (header != null) {
				String name = header.substring(0, header.indexOf(": "));
				String value = header.substring(name.length() + 2);
				req.headers.put(name, value);
			}
			br.close();
			
			//检查Header结束的位置
			br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(buf, 0, firstLen)));
			br.readLine();
			br.readLine();
			//while
			return req;
		}
	}
	
	class HttpReq {
		String method;
		String uri;
		String queryString;
		Properties headers = new Properties();
		InputStream in;
	}
}
