package org.nutz.http.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.http.server.NutWebContext;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Https {

	private static final Log log = Logs.get();
	
	public static SimpleDateFormat httpDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
	public static Date httpData(String str) throws ParseException {
		return httpDateFormat.parse(str);
	}
	public static String httpDate(Date date) {
		return httpDateFormat.format(date);
	}
	
	public static NutHttpReq makeHttpReq(NutWebContext ctx, final Socket socket, byte[] head, final byte[] preRead) throws IOException {

		NutHttpReq req = new NutHttpReq();
		
		//log.debug("head -->\n" + new String(head));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(head);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		//解析请求行
		String reqLine = br.readLine();
		if (reqLine == null)
			throw new IllegalArgumentException("Bad req!");
		String[] _tmps = reqLine.split(" ");
		if (_tmps.length != 3 || _tmps[0] == null || _tmps[1] == null)
			throw new IllegalArgumentException("Bad req! --> " + reqLine);
			
		req.method = _tmps[0].intern();
		req.originalURI = _tmps[1].intern();
		//解析Headers
		while (br.ready()) {
			String headerLine = br.readLine();
			if (headerLine == null)
				break;
			headerLine = headerLine.trim().intern();
			if (!headerLine.contains(":") || headerLine.startsWith(":") || headerLine.endsWith(":"))
				continue; //Bad http header
			String name = headerLine.substring(0, headerLine.indexOf(":")).trim();
			String value = headerLine.substring(headerLine.indexOf(":") + 1).trim().trim();
			
			//检测一下非法的header值
			if (name.contains("_") || name.length() > 48 || value.length() > 128)
				throw new IllegalArgumentException("Bad req header --> " + headerLine);
			req.headers().add(name, value);
		}
		
		//生成输入InputStream
		if (preRead == null || preRead.length == 0) {
			req.in = socket.getInputStream();
		} else {
			req.in = new InputStream() {
				
				private int pos;
				private InputStream in;
				private byte[] _preRead = preRead;
				
				public int read(byte[] b, int off, int len) throws IOException {
					if (in != null)
						return in.read(b, off, len);
					return super.read(b, off, len);
				}
				
				public int read() throws IOException {
					if (in != null)
						return in.read();
					if (_preRead != null) {
						if (pos < preRead.length - 1)
							return preRead[pos++];
						_preRead = null;
					}
					in = socket.getInputStream();
					return in.read();
				}
			};
		}
		
		req.resp = new NutHttpResp();
		req.resp.req = req;
		req.resp.out = socket.getOutputStream();
		req.socket = socket;
		req.ctx = ctx;
		
		req.analysisOriginalURI();
		//req.analysisParameters();  在容器中处理!!
		
		log.debug("Req create complete --> " + req.requestURI);
		
		return req;
	}
}
