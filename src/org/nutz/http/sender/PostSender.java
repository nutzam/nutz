package org.nutz.http.sender;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.Map;

import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Streams;

public class PostSender extends Sender {

	public PostSender(Request request) {
		super(request);
	}

	@Override
	public Response send() throws HttpException {
		try {
			openConnection();
			Map<String, ?> params = request.getParams();
			String data = null;
			if (null != params && params.size() > 0) {
				data = request.getURLEncodedParams();
			}
			setupRequestHeader();
			setupDoInputOutputFlag();
			if (data != null) {
				Writer w = new BufferedWriter(Streams.utf8w(conn.getOutputStream()));
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
