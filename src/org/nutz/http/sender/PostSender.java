package org.nutz.http.sender;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.http.Http;
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
			setupRequestHeader();
			setupDoInputOutputFlag();
			Writer w = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
			Map<String, ?> params = request.getParams();
			if (null != params && params.size() > 0) {
				for (Entry<String,?> entry : params.entrySet()) {
					w.write(Http.encode(entry.getKey()));
					w.write('=');
					w.write(Http.encode(entry.getValue()));
					w.write('&');
				}
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
