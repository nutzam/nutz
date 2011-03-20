package org.nutz.mvc.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.el.El;
import org.nutz.el.ElObj;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.lang.util.Context;
import org.nutz.mvc.Mvcs;
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

	private Map<String, ElObj> exps;

	public ServerRedirectView(String dest) {
		this.dest = new CharSegment(Strings.trim(dest));
		this.exps = new HashMap<String, ElObj>();
		// 预先将每个占位符解析成表达式
		for (String key : this.dest.keys()) {
			ElObj exp = El.compile(key);
			this.exps.put(key, exp);
		}
	}

	public void render(HttpServletRequest req, HttpServletResponse resp, Object obj)
			throws Exception {

		String path = evalPath(req, obj);

		// Another site
		if (path.startsWith("http://") || path.startsWith("https://")) {}
		// Absolute path, add the context path for it
		else if (path.length() > 0 && path.charAt(0) == '/') {
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

	protected String evalPath(HttpServletRequest req, Object obj) {
		Context context = Lang.context();

		// 解析每个表达式
		Context expContext = Mvcs.createContext(req, obj);
		for (Entry<String, ElObj> en : exps.entrySet()) {
			context.set(en.getKey(), en.getValue().eval(expContext).getString());
		}

		// 生成解析后的路径
		String path = this.dest.render(context).toString();
		return path;
	}
}
