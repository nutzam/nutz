package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionInvoking {

	public ActionInvoking(ActionInvoker invoker, String[] pathArgs) {
		this.invoker = invoker;
		this.pathArgs = pathArgs;
	}

	private ActionInvoker invoker;

	private String[] pathArgs;

	public ActionInvoker getInvoker() {
		return invoker;
	}

	public void setInvoker(ActionInvoker invoker) {
		this.invoker = invoker;
	}

	public String[] getPathArgs() {
		return pathArgs;
	}

	public void setPathArgs(String[] pathArgs) {
		this.pathArgs = pathArgs;
	}

	public void invoke(HttpServletRequest req, HttpServletResponse resp) {
		invoker.invoke(req, resp, pathArgs);
	}

}
