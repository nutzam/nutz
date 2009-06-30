package org.nutz.mvc.view;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.mvc.View;

public class RedirectView implements View {

	private Segment dest;

	public RedirectView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
	}

	@Override
	public void render(HttpServletRequest request, HttpServletResponse response, Object obj)
			throws Exception {
		Mirror<?> me = null;
		if (null != obj)
			me = Mirror.me(obj.getClass());

		for (Iterator<String> it = dest.keys().iterator(); it.hasNext();) {
			String key = it.next();
			Object value = null;
			if (null != me && key.startsWith("obj.")) {
				value = me.getValue(obj, key.substring(4));
			} else {
				value = request.getParameter(key);
			}
			dest.set(key, value);
		}
		String path = dest.toString();
		if (path.startsWith("/"))
			path = request.getContextPath() + path;
		else {
			String myPath = request.getServletPath();
			int pos = myPath.lastIndexOf('/');
			path = myPath.substring(1, pos) + "/" + path;
		}
		// String url = String.format(
		// "<HTML><HEAD><META HTTP-EQUIV=Refresh CONTENT=\"0; URL=%s\"></HEAD></HTML>",
		// path);
		// response.getWriter().write(url);
		response.sendRedirect(path);
		response.flushBuffer();
	}
}
