package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.init.PathInfo;

public class ActionInvoking {

	public ActionInvoking(PathInfo<ActionInvoker> pathInfo, String[] pathArgs) {
		this.pathInfo = pathInfo;
		this.pathArgs = pathArgs;
	}

	private PathInfo<ActionInvoker> pathInfo;

	private String[] pathArgs;

	public ActionInvoker getInvoker() {
		return pathInfo.getObj();
	}

	public String[] getPathArgs() {
		return pathArgs;
	}

	public PathInfo<ActionInvoker> getPathInfo() {
		return pathInfo;
	}

	public void setPathArgs(String[] pathArgs) {
		this.pathArgs = pathArgs;
	}

	public void invoke(HttpServletRequest req, HttpServletResponse resp) {
		getInvoker().invoke(req, resp, pathArgs);
	}

}
