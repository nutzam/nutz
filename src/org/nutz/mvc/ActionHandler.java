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
		ActionContext ac = new ActionContext();
		ac.setRequest(req).setResponse(resp).setServletContext(config.getServletContext());

		ActionChain chain = mapping.get(ac, req);
		if (null == chain)
			return false;

		chain.doChain(ac);
		return true;
	}

	public void depose() {
		loading.depose(config);
	}

}
