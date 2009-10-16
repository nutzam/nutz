package org.nutz.mvc.view;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.segment.Segments;
import org.nutz.mvc.View;

public class ServerRedirectView implements View {

	private Segment dest;

	public ServerRedirectView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		Mirror<?> me = null;
		if (null != obj)
			me = Mirror.me(obj.getClass());

		// Prepare the dest path ...
		// If object is not null, fill the dest by it
		if (null != obj)
			Segments.fillByKeys(dest, obj);
		else
			// else fill by request params
			for (Iterator<String> it = dest.keys().iterator(); it.hasNext();) {
				String key = it.next();
				Object value = null;
				if (null != me && key.startsWith("obj.")) {
					value = me.getValue(obj, key.substring(4));
				} else {
					value = req.getParameter(key);
				}
				dest.set(key, value);
			}
		// Format the path ...
		String path = dest.toString();
		// Absolute path, add the context path for it
		if (path.startsWith("/")) {
			path = req.getContextPath() + path;
		}
		// Relative path, add current URL path for it
		else {
			String myPath = req.getPathInfo();
			int pos = myPath.lastIndexOf('/');
			if (pos > 0)
				path = myPath.substring(0, pos) + "/" + path;
			else
				path = "/" + path;
		}
		resp.sendRedirect(path);
		resp.flushBuffer();
	}

}
