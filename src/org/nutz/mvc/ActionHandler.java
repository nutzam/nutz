package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ActionHandler {

	private Loading loading;

	private UrlMapping mapping;

	private NutConfig config;

	public ActionHandler(NutConfig config) {
		this.config = config;
		this.loading = config.createLoading();
		this.mapping = loading.load(config);
	}

	public boolean handle(HttpServletRequest req, HttpServletResponse resp) {
		String path = Mvcs.getRequestPath(req);
		ActionContext ac = new ActionContext();
		ac.setRequest(req);
		ac.setResponse(resp);
		ac.setServletContext(config.getServletContext());

		ActionChain chain = mapping.get(ac, path);
		if (null == chain)
			return false;

		chain.doChain(ac);
		return true;
	}

	public void depose() {
		loading.depose(config);
	}

}
