package org.nutz.mvc.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.mvc.View;

/**
 * 将数据对象直接写入 HTTP 响应
 * <p>
 * <h2>数据对象可以是如下类型:</h2>
 * <ol>
 * <li><b>null</b> - 什么都不做
 * <li><b>byte[]</b> - 按二进制方式写入HTTP响应流
 * <li><b>InputStream</b> - 按二进制方式写入响应流，并关闭 InputStream
 * <li><b>char[]</b> - 按文本方式写入HTTP响应流
 * <li><b>Reader</b> - 按文本方式写入HTTP响应流，并关闭 Reader
 * <li><b>默认的</b> - 直接将对象 toString() 后按文本方式写入HTTP响应流
 * </ol>
 * <p>
 * <h2>ContentType 支持几种缩写:</h2>
 * <ul>
 * <li><b>xml</b> - 表示 <b>text/xml</b>
 * <li><b>html</b> - 表示 <b>text/html</b>
 * <li><b>htm</b> - 表示 <b>text/html</b>
 * <li><b>stream</b> - 表示 <b>application/octet-stream</b>
 * <li><b>默认的</b>(即 '@Ok("raw")' ) - 将采用 <b>ContentType=text/plain</b>
 * </ul>
 * 
 * @author wendal(wendal1985@gmail.com)
 * @author zozoh(zozohtnt@gmail.com)
 * 
 */
public class RawView implements View {

	private String contentType;

	public RawView(String contentType) {
		if (Strings.isBlank(contentType))
			contentType = "text/plain";
		this.contentType = Strings.sNull(contentTypeMap.get(contentType.toLowerCase()), contentType);
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Throwable {
		resp.setContentType(contentType);
		if (obj == null)
			return;
		// 字节数组
		if (obj instanceof byte[]) {
			OutputStream os = resp.getOutputStream();
			os.write((byte[]) obj);
			os.flush();
		}
		// 字符数组
		else if (obj instanceof char[]) {
			Writer writer = resp.getWriter();
			writer.write((char[]) obj);
			writer.flush();
		}
		// 文本流
		else if (obj instanceof Reader) {
			Writer w = resp.getWriter();
			Reader r = (Reader) obj;
			char[] cbuf = new char[8192];
			int len;
			try {
				while (-1 != (len = r.read(cbuf))) {
					w.write(cbuf, 0, len);
				}
				resp.flushBuffer();
			}
			finally {
				Streams.safeClose(w);
				Streams.safeClose(r);
			}
		}
		// 二进制流
		else if (obj instanceof InputStream) {
			OutputStream out = resp.getOutputStream();
			InputStream ins = (InputStream) obj;
			byte[] buf = new byte[8192];
			int len;
			try {
				while (-1 != (len = ins.read(buf))) {
					out.write(buf, 0, len);
				}
			}
			finally {
				Streams.safeClose(out);
				Streams.safeClose(ins);
			}
		}
		// 普通对象
		else {
			Writer writer = resp.getWriter();
			writer.write(String.valueOf(obj));
			writer.flush();
		}
	}

	private static final Map<String, String> contentTypeMap = new HashMap<String, String>();

	static {
		contentTypeMap.put("xml", "text/xml");
		contentTypeMap.put("html", "text/html");
		contentTypeMap.put("htm", "text/html");
		contentTypeMap.put("stream", "application/octet-stream");
		contentTypeMap.put("js", "text/javascript");
		contentTypeMap.put("json", "text/javascript");
	}
}
