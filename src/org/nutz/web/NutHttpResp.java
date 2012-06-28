package org.nutz.web;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import org.nutz.Nutz;

/**
 * Nutz对Http响应的理解:
 * <ul>
 * 响应码(200,302,404等)
 * </ul>
 * <ul>
 * 响应信息(例如200对应OK,等等)
 * </ul>
 * <ul>
 * Headers,头部信息
 * </ul>
 * <ul>
 * 响应体(Body)
 * </ul>
 * </p>其他信息,都是上述信息的进一步约定而已
 * 
 * @author wendal
 * 
 */
public class NutHttpResp extends HttpMessage {

	//------------------------------------------------
	/**
	 * 响应码
	 */
	protected int status = 200;//默认200
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	/**
	 * 响应信息
	 */
	protected String msg = "OK"; //默认为OK
	public String msg() {
		return msg;
	}
	
	/*Header信息由HttpMessage提供*/
	
	//---------------------------------------------------
	/**
	 * 响应体,由OutputStreamt表示
	 */
	protected OutputStream out;
	public OutputStream getOutputStream() {
		return out;
	}
	public void setOutputStream(OutputStream out) {
		this.out = out;
	}
	
	
	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	// 以下均为基本信息延伸的结果
	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	
	protected boolean headerSent;
	/**
	 * 发送响应头部
	 */
	public void sendRespHeaders() throws IOException {
		if (!headerSent) {
			// 补充几个附加的头,调试用
			headers.add("X-Power-By", "Nutz Http Server " + Nutz.version());
			headers.addDate("ServerTime", System.currentTimeMillis());
			headers.set("Connection", "close"); //暂不支持长连接
			
			//发送响应头
			out.write(("HTTP/1.1 "+status+" " + msg + "\r\n").getBytes());
			for (Entry<String, List<String>> header : headers.datas().entrySet()) {
				for (String headerValue : header.getValue()) {
					out.write(header.getKey().getBytes());
					out.write(": ".getBytes());
					out.write(headerValue.getBytes());
					out.write("\r\n".getBytes());
				}
			}
			out.write("\r\n".getBytes());
			out.flush();
		}
	}
	
	public void sendAndClose(String body) throws IOException {
		if (body != null && body.length() > 0) {
			setContentLength(body.getBytes().length);
		}
		sendRespHeaders();
		if (body != null && body.length() > 0) {
			out.write(body.getBytes());
		}
		out.flush();
		out.close();
	}
	
	public void sendError(int status, String msg, String body) throws IOException {
		this.status = status;
		this.msg = msg;
		sendAndClose(body);
	}
	
	public void sendRedirect(String path) throws IOException {
		if (path == null)
			throw new NullPointerException("path is null");
		status = 302;
		msg = "Redirect";
		headers.set("Location", path);
		sendAndClose(null);
	}
	
	//--------------------------------------------------------------------------------
	// 附加属性
	protected NutHttpReq req;
	public NutHttpReq req() {
		return req;
	}
	public void setReq(NutHttpReq req) {
        this.req = req;
    }
}
