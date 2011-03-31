package org.nutz.http.sender;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.util.Map;

import org.nutz.http.Header;
import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;

public class PostSender extends Sender {

	public PostSender(Request request) {
		super(request);
	}

	@Override
	public Response send() throws HttpException {
		try {
			openConnection();
			HttpURLConnection hu = (HttpURLConnection)conn;
			hu.setRequestMethod("POST");
			Header header = request.getHeader();
			if (header == null) {
				header = Header.create();
				request.setHeader(header);
			}
			Map<String, ?> params = request.getParams();
			String data = null;
			if (null != params && params.size() > 0) {
				data = request.getURLEncodedParams();
				header.set("Content-Type", "application/x-www-form-urlencoded");
				header.set("Content-Length", ""+data.length());
			}
			setupRequestHeader();
			setupDoInputOutputFlag();
			if (data != null) {
				Writer w = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(),Encoding.CHARSET_UTF8));
				w.write(data);
				Streams.safeFlush(w);
				Streams.safeClose(w);
			}
			return createResponse(getResponseHeader());
		}
		catch (Exception e) {
			throw new HttpException(request.getUrl().toString(), e);
		}
	}

}
