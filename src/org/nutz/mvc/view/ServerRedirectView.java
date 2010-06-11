package org.nutz.mvc.view;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.mvc.View;

/**
 * 重定向视图
 * <p>
 * 在入口函数上声明：
 * <p>
 * '@Ok("redirect:/pet/list.nut")'
 * <p>
 * 实际上相当于：<br>
 * new ServerRedirectView("/pet/list.nut");
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ServerRedirectView implements View {

	private Segment dest;

	public ServerRedirectView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
	}

	// TODO 这个函数写的有点烂，有时间重构一下
	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {
		Mirror<?> mirror = Mirror.me(obj);
		boolean isMap = null != obj && obj instanceof Map<?, ?>;
		Map<?, ?> map = isMap ? (Map<?, ?>) obj : null;

		// Fill path
		Set<String> keySet = dest.keys();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			Object value = null;
			int length = key.length();
			if (key.startsWith("p.") && length > 2) {
				value = req.getParameter(key.substring(2));
			}
			// Map
			else if (isMap && key.startsWith("obj.") && length > 4) {
				value = map.get(key);
			}
			// POJO
			else if (null != mirror && key.startsWith("obj.") && length > 4) {
				value = mirror.getValue(obj, key.substring(4));
			}
			// Normal value
			else {
				value = obj;
			}
			if (null == value)
				value = obj;
			dest.set(key, value);
		}

		// Format the path ...
		String path = dest.toString();

		// Another site
		if (path.startsWith("http://") || path.startsWith("https://")) {}
		// Absolute path, add the context path for it
		else if (path.startsWith("/")) {
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
