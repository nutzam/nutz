package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.View;

public class ViewProcessor extends AbstractProcessor {

	protected View view;
	
	@Override
	public void init(NutConfig config, ActionInfo ai) throws Throwable {
		view = evalView(config, ai, ai.getOkView());
	}

	public void process(ActionContext ac) throws Throwable {
		Object re = ac.getMethodReturn();
		Object err = ac.getError();
		if (re instanceof View) {
			((View) re).render(ac.getRequest(), ac.getResponse(), err);
		} else {
			view.render(ac.getRequest(), ac.getResponse(), null == re ? err : re);
		}
		doNext(ac);
	}

}
