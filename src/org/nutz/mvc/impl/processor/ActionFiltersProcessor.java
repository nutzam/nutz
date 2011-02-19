package org.nutz.mvc.impl.processor;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.impl.Processor;

public class ActionFiltersProcessor implements Processor {

	private ActionFilter[] filters;

	public ActionFiltersProcessor(ActionFilter[] filters) {
		this.filters = filters;
	}

	public boolean process(ActionContext ac) throws Throwable {
		ServletContext sc = ac.getServletContext();
		HttpServletRequest req = ac.getRequest();
		Method method = ac.getMethod();
		View view;
		for (ActionFilter filter : filters) {
			view = filter.match(sc, req, method);
			if (null != view) {
				Object obj = ac.getError();
				if (null == obj)
					obj = ac.getMethodReturn();
				view.render(req, ac.getResponse(), obj);
				return false;
			}
		}
		return true;
	}

}
