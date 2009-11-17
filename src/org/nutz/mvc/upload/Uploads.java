package org.nutz.mvc.upload;

import javax.servlet.http.HttpServletRequest;

public abstract class Uploads {

	public static UploadInfo getInfo(HttpServletRequest req) {
		return (UploadInfo) req.getSession().getAttribute(UploadInfo.class.getName());
	}

}
