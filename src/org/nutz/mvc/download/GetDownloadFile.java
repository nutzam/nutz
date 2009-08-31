package org.nutz.mvc.download;

import java.io.File;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Controllor;

public class GetDownloadFile implements Controllor {

	private File home;

	public void setHome(File file) {
		this.home = file;
	}

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String qs = request.getQueryString();
		qs = URLDecoder.decode(qs, "UTF-8");
		return new File(home.getAbsolutePath() + qs);
	}

}
