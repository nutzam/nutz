package org.nutz.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import org.nutz.lang.Lang;

public class Response {

	public Response(Map<String, String> reHeader) {
		// HTTP/1.1 404 /test/cc/yyt
		String[] codes = reHeader.get(null).split(" ");
		protocal = codes[0];
		status = Integer.parseInt(codes[1]);
		if (codes.length >= 3)
			detail = codes[2];
		this.header = Header.create(reHeader);
		String s = header.get("Set-Cookie");
		if (null != s)
			this.cookie = new Cookie(s);
	}

	private Header header;
	private InputStream stream;
	private Cookie cookie;
	private String protocal;
	private int status;
	private String detail;

	public String getProtocal() {
		return protocal;
	}

	public int getStatus() {
		return status;
	}

	public String getDetail() {
		return detail;
	}

	public boolean isOK() {
		return status == 200;
	}

	public boolean isServerError() {
		return status >= 500 && status < 600;
	}

	public boolean isClientError() {
		return status >= 400 && status < 500;
	}

	void setStream(InputStream stream) {
		this.stream = stream;
	}

	public Header getHeader() {
		return header;
	}

	public InputStream getStream() {
		return new BufferedInputStream(stream);
	}

	public Reader getReader() {
		try {
			return new InputStreamReader(getStream(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void printHeader(Writer writer) {
		try {
			writer.write(header.toString());
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void print(Writer writer) {
		try {
			Reader reader = this.getReader();
			writer.write("\r\n");
			int c;
			while (-1 != (c = reader.read())) {
				writer.write(c);
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}

	}

	public String getContent() {
		StringBuilder sb = new StringBuilder();
		Writer w = Lang.opw(sb);
		print(w);
		return sb.toString();
	}
}
