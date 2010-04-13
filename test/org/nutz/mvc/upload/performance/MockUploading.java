package org.nutz.mvc.upload.performance;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.nutz.http.Http;
import org.nutz.lang.Lang;

public class MockUploading {

	private static final int defaultBufferSize = 8 * 1024;

	// private String encoding;
	private Map<String, Object> params;
	private String boundary;
	private ServletInputStream ins;
	private File destFile;

	public void parse(HttpServletRequest request, File destFile) {
		this.destFile = destFile;
		boolean endFlag = false;
		String boundaryExt = this.boundary + "\r\n";
		int beSize = boundaryExt.length();
		// int bSize = boundary.length();
		try {
			String line = this.readRealLine();
			if (line == null)
				return;
			// 第二行:一定是头部
			NFieldMeta nfm = new NFieldMeta(line);
			while (true) {
				if (endFlag)
					break;
				if (nfm.isFile()) {
					// Content-Type: text/plain
					line = this.readRealLine();
					// \r\n,去掉之后""
					line = this.readRealLine();
					// 开始读文件正文
					byte[] readBuff = new byte[65536];
					int size = readBuff.length;
					destFile.delete();
					destFile.createNewFile();
					OutputStream ops = new BufferedOutputStream(
							new FileOutputStream(destFile));
					do {
						int re = this.ins.readLine(readBuff, 0, size);
						if (re == size) {
							ops.write(readBuff, 0, re);
						} else if (re > 0) {
							if (re == beSize) {
								// 边界：boundary+"\r\n"
								byte[] copy = new byte[re];
								System.arraycopy(readBuff, 0, copy, 0, re);
								String s = new String(copy);
								if (s.equals(boundaryExt)) {
									line = this.readRealLine();
									nfm = new NFieldMeta(line);
									ops.close();
									continue;
								} else {
									ops.write(readBuff, 0, re);
									continue;
								}
							} else if (re == beSize + 2) {
								// 结束符：boundary+"--\r\n"
								byte[] copy = new byte[re];
								System.arraycopy(readBuff, 0, copy, 0, re);
								String s = new String(copy);
								if (s.equals(boundary + "--\r\n")) {
									ops.close();
									endFlag = true;
									break;
								} else {
									ops.write(readBuff, 0, re);
									continue;
								}
							} else {
								ops.write(readBuff, 0, re);
								continue;
							}
						}
					} while (true);

				} else {
					// \r\n,去掉之后""
					line = this.readRealLine();
					// value
					line = this.readRealLine();
					params.put(nfm.getName(), line);
					line = this.readRealLine();
					if (line.equals(boundary)) {
						line = this.readRealLine();
						nfm = new NFieldMeta(line);
					} else if (line.equals((boundary) + "--")) {
						// 结束符，结束返回
						return;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String readRealLine() throws IOException {
		byte[] buf = new byte[8 * 1024];
		StringBuilder sb = new StringBuilder();
		int result;
		do {
			result = ins.readLine(buf, 0, buf.length); // does +=
			if (result != -1) {
				sb.append(new String(buf, 0, result, "UTF-8"));
			}
		} while (result == buf.length); // loop only if the buffer was filled

		if (sb.length() == 0) {
			return null; // nothing read, must be at the end of stream
		}

		// Cut off the trailing \n or \r\n
		// It should always be \r\n but IE5 sometimes does just \n
		// Thanks to Luke Blaikie for helping make this work with \n
		int len = sb.length();
		if (len >= 2 && sb.charAt(len - 2) == '\r') {
			sb.setLength(len - 2); // cut \r\n
		} else if (len >= 1 && sb.charAt(len - 1) == '\n') {
			sb.setLength(len - 1); // cut \n
		}
		return sb.toString();
	}

	public MockUploading(HttpServletRequest request) {
		this(request, defaultBufferSize);
	}

	public MockUploading(HttpServletRequest request, int bufferSize) {
		// this.encoding = charset;
		this.params = new HashMap<String, Object>();
		try {
			this.boundary = "--"
					+ Http.multipart.getBoundary(request.getContentType());
			ServletInputStream in = request.getInputStream();
			if (bufferSize <= 0) {
				this.ins = new BufferedServletInputStream(in);
			} else {
				this.ins = new BufferedServletInputStream(in, bufferSize);
			}
			// Read until we hit the boundary
			// Some clients send a preamble (per RFC 2046), so ignore that
			// Thanks to Ben Johnson, ben.johnson@merrillcorp.com, for pointing
			// out
			// the need for preamble support.
			do {
				String line = readRealLine();
				if (line == null) {
					throw new IOException("Corrupt form data: premature ending");
				}
				// See if this line is the boundary, and if so break
				if (line.startsWith(boundary)) {
					break; // success
				}
			} while (true);
		} catch (IOException e) {
			Lang.makeThrow("Upload init Exception.");
		}
	}

}
