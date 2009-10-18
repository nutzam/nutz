package org.nutz.mvc.module;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.CachedDownloadView;
import org.nutz.mvc.view.DownloadView;

/**
 * 抽象的 Nutz.Mvc 默认模块。
 * <p>
 * 你可以从这个模块继承你的默认模块，它提供了默认 url 处理，默认处理各种 MIME 类型。
 * 不过大多数情况，你的默认模块并不需要从这个模块继承。这个类的代码可以作为你的一个参考
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public abstract class NutModule {

	protected Map<String, View> cache;
	protected Set<String> mimes;
	protected View defaultView;

	protected NutModule() {
		mimes = new HashSet<String>();
		cache = new HashMap<String, View>();
		defaultView = new DownloadView();
		init();
	}

	protected void init() {
		putCachedMimes("txt", "html", "gif", "jpg", "jpeg", "png", "avi", "wma", "rm", "swf", "fla");
	}

	protected void putCachedMimes(String... types) {
		for (String type : types)
			mimes.add(type);
	}

	protected NutModule clearMimeTypes() {
		cache.clear();
		return this;
	}

	@At("*")
	@Ok("http:404")
	@Fail("http:500")
	public View mimes(HttpServletRequest req, ServletContext context) {
		String path = req.getPathInfo();
		if (path.toUpperCase().startsWith("/WEB-INF/")) {
			return null;
		}
		// Try to find the MIME type
		String mime = null;
		for (int i = path.length() - 2; i > 0; i--) {
			char c = path.charAt(i);
			if (c == '.') {
				mime = path.substring(i + 1);
				break;
			}
			if (c == '/')
				break;
		}
		File f = new File(context.getRealPath(path));
		if (f.exists()) {
			req.setAttribute("mime", f);
			return findView(mime.toLowerCase(), path);
		}
		return null;
	}

	protected View findView(String mime, String path) {
		View re = cache.get(path);
		if (null == re)
			if (mimes.contains(mime)) {
				re = new CachedDownloadView();
				cache.put(path, re);
			}
		return re == null ? this.defaultView : re;
	}

}
