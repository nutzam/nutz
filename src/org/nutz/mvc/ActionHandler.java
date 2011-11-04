package org.nutz.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.impl.ActionInvoker;

public class ActionHandler {

	private Loading loading;

	private UrlMapping mapping;

	private NutConfig config;

	public ActionHandler(NutConfig config) {
		this.config = config;
		this.loading = config.createLoading();
		this.mapping = loading.load(config);
		//加载nutz配置
		org.nutz.conf.NutConf.load();
	}

	public boolean handle(HttpServletRequest req, HttpServletResponse resp) {
		ActionContext ac = new ActionContext();
		ac.setRequest(req).setResponse(resp).setServletContext(config.getServletContext());

		ActionInvoker invoker = mapping.get(ac);
		if (null == invoker)
			return false;

		return invoker.invoke(ac);
	}

	public void depose() {
		loading.depose(config);
	}

}
