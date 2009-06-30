package org.nutz.http.sender;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import org.nutz.http.Http;
import org.nutz.http.HttpException;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;

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
				for (Iterator<String> it = params.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					w.write(Http.encode(key));
					w.write('=');
					w.write(Http.encode(params.get(key)));
					if (it.hasNext())
						w.write('&');
				}
				w.flush();
				w.close();
				w = null;
			}
			return createResponse(getResponseHeader());

		} catch (Exception e) {
			throw new HttpException(request.getUrl().toString(), e);
		}
	}

}
