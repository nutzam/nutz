package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.View;

public class ViewProcessor extends AbstractProcessor {

	private View view;

	public ViewProcessor(View view) {
		this.view = view;
	}

	public void doProcess(ActionContext ac) throws Throwable {
		Object re = ac.getMethodReturn();
		Object err = ac.getError();
		if (re instanceof View) {
			((View) re).render(ac.getRequest(), ac.getResponse(), err);
		} else {
			view.render(ac.getRequest(), ac.getResponse(), null == re ? err : re);
		}
	}

}
