package org.nutz.mvc.module;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.view.MIMEView;

public abstract class NutModule {

	private Map<String, Borning<? extends MIMEView>> mimes;

	protected NutModule() {
		mimes = new HashMap<String, Borning<? extends MIMEView>>();
		addMime("txt", "html", "gif", "jpg", "jpeg", "png", "avi", "wma", "rm", "swf", "fla");
	}

	private void addMime(String... types) {
		for (String type : types)
			addMime(type, MIMEView.class, new File(""));
	}

	protected NutModule clearMime() {
		mimes.clear();
		return this;
	}

	protected NutModule addMime(String mime, Class<? extends MIMEView> type, Object... args) {
		Borning<? extends MIMEView> borning = Mirror.me(type).getBorning(args);
		mimes.put(mime.toLowerCase(), borning);
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
			Borning<? extends MIMEView> borning = findView(mime.toLowerCase());
			return borning.born(Lang.array(path));
		}
		return null;
	}

	protected Borning<? extends MIMEView> findView(String mime) {
		return mimes.get(mime);
	}

}
