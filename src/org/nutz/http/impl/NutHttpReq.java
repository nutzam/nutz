package org.nutz.http.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;

/**
 * Nutz对Http请求的理解:
 * <ul>
 * 请求方法(GET/POST等)
 * </ul>
 * <ul>
 * 请求的URI
 * </ul>
 * <ul>
 * Headers,头部信息
 * </ul>
 * <ul>
 * 请求体(Body)
 * </ul>
 * </p>其他信息,都是上述信息的进一步约定而已
 * 
 * @author wendal
 * 
 */
public class NutHttpReq extends HttpMessage {

	/**
	 * 请求方法
	 */
	protected String method;

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * 请求的URI,原始的,未经URLDecoder进行翻译
	 */
	protected String originalURI;

	public String getOriginalURI() {
		return originalURI;
	}

	public void setOriginalURI(String originalURI) {
		this.originalURI = originalURI;
	}

	/* Header由HttpMessage进行描述,并延伸出Cookie,Session等基于Headers的扩展 */

	// ----------------------------------------------------------------
	/**
	 * 请求体,使用InputStream表示
	 */
	protected InputStream in;

	public InputStream getInputStream() {
		return in;
	}

	public void setInputStream(InputStream in) {
		this.in = in;
	}

	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------
	// 以下均为基本信息延伸的结果
	// ----------------------------------------------------------------------------
	// ----------------------------------------------------------------------------

	//----------------------------------------------------------------------
	// 分析原始URI,解出queryString和真正的请求URI
	protected String queryString;
	public String queryString() {
		return queryString;
	}
	protected String requestURI;
	public String requestURI() {
		return queryString;
	}

	/**
	 *  分析原始URI,解出queryString和真正的请求URI
	 */
	protected void analysisOriginalURI() {
		if (requestURI != null)
			return; //已经分析过了
		String ol = originalURI;
		if (originalURI.contains(";")) { // 抛弃;后面的数据
			ol = originalURI.substring(0, originalURI.indexOf(";"));
		}
		try {
			ol = URLDecoder.decode(ol, "iso-8891-1");// TODO 改成可配置的
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
		if (ol.contains("?")) { // 带参数的
			int i = ol.indexOf("?");
			if (i != ol.length() - 1)
				queryString = ol.substring(i + 1);
			requestURI = ol.substring(0, i);
		} else {
			requestURI = ol;
		}

		// 检查requestURI
		if (requestURI.startsWith("/") || requestURI.contains("/../")) {
			// TODO 扔个异常什么的?
		}
	}

	// ----------------------------------------------------------------
	// 分析请求参数
	protected Map<String, List<String>> params = new HashMap<String, List<String>>();
	public Map<String, List<String>> params() {
		return params;
	}

	public String getParameter(String name) {
		List<String> obj = params.get(name);
		if (obj == null || obj.isEmpty())
			return null;
		return obj.get(0);
	}

	public List<String> getParameterValues(String name) {
		return params.get(name);
	}
	
	/**
	 * 分析请求参数
	 */
	public void analysisParameters() throws IOException {
		if (!params.isEmpty())
			return; //已经分析过了
		
		if (queryString != null) {
			//解析URL中的参数
			resolveParameters(queryString, "");
		}
		
		if (!"POST".endsWith(method))
			return; //只有POST才会有请求体的参数
		
		if (getContentLength() < 1)
			return; //有明确请求体的,才解析
		
		if (getContentLength() > 8192)
			return; //多于8kb的POST数据,俺不解析了!!
		
		String contentType = getContentType();
		if (contentType == null || !contentType.contains("application/x-www-form-urlencoded"))
			return; //只有Content-Type是
		String contentEncoding = "UTF-8";
		if (contentType.contains(";"))
			contentEncoding = contentType.substring(contentType.indexOf(";") + 1);
		
		//先读取100个字符,找找=号,如果没有,那就是假的!这是不对的!!
		PushbackInputStream pis = new PushbackInputStream(in, 300);
		InputStreamReader reader = new InputStreamReader(pis, contentEncoding);
		CharBuffer buf = CharBuffer.allocate(100);
		int len = reader.read(buf);
		if (len < 1)
			return; //没数据?!不太可能吧
		String str = new String(buf.array());
		if (!str.contains("=")) {
			//没有包含?! 我%*&
			pis.unread(str.getBytes(contentEncoding));
			in = pis;
		} else {
			//嘿嘿,来吧,重头戏!!!
			StringBuilder sb = new StringBuilder(str);
			buf.clear();
			while (reader.ready()) {
				if (reader.read(buf) < 0)
					break;
				sb.append(new String(buf.array()));
				buf.clear();
			}
			resolveParameters(sb.toString(), contentEncoding);
		}
	}
	
	// ------------------------------------------------------------------------------
	protected void resolveParameters(String str, String enc) {
		if (str == null || str.isEmpty())
			return;
		String[] pairs = str.split("&");
		for (String pair : pairs) {
			if (pair == null || pair.isEmpty())
				continue;
			if (pair.startsWith("="))
				continue;
			if (pair.endsWith("="))
				addHeader(pair.substring(0, pair.length() - 1).intern(), "");
			else
				try {
					if (enc.isEmpty())
						addHeader(pair.substring(0, pair.indexOf('=')), pair.substring(pair.indexOf('=') + 1));
					else
						addHeader(pair.substring(0, pair.indexOf('=')), URLDecoder.decode(pair.substring(pair.indexOf('=') + 1), enc));
				} catch (UnsupportedEncodingException e) {
					throw Lang.wrapThrow(e);
				}
		}
	}
	
	//-----------------------------------------------------------------------------
	// 额外属性
	protected NutHttpResp resp;
	public NutHttpResp resp() {
		return resp;
	}
	
	protected Socket socket;
	public Socket socket() {
		return socket;
	}
}
