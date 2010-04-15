package org.nutz.mvc.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public abstract class Uploads {

	public static UploadInfo getInfo(HttpServletRequest req) {
		return (UploadInfo) req.getSession().getAttribute(UploadInfo.class.getName());
	}

	public static UploadInfo createInfo(HttpServletRequest req) {
		UploadInfo info = new UploadInfo();
		HttpSession sess = req.getSession();
		if (null != sess) {
			sess.setAttribute(UploadInfo.SESSION_NAME, info);
		}
		info.sum = req.getContentLength();
		return info;
	}

}
