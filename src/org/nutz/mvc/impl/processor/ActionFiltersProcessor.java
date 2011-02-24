package org.nutz.mvc.impl.processor;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;

public class ActionFiltersProcessor extends AbstractProcessor {

	private ActionFilter[] filters;

	public ActionFiltersProcessor(ActionFilter[] filters) {
		this.filters = filters;
	}

	public void process(ActionContext ac) throws Throwable {
		View view;
		for (ActionFilter filter : filters) {
			view = filter.match(ac);
			if (null != view) {
				Object obj = ac.getError();
				if (null == obj)
					obj = ac.getMethodReturn();
				view.render(ac.getRequest(), ac.getResponse(), obj);
				return;
			}
		}
		doNext(ac);
	}

}
