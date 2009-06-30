package org.nutz.mvc.entity;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Mirror;
import org.nutz.mvc.Controllor;
import org.nutz.mvc.HttpCondition;
import org.nutz.service.EntityService;

public abstract class ConditionControllor implements Controllor {

	protected ConditionControllor() {}

	protected ConditionControllor(EntityService<?> service) {
		this();
		this.service = service;
	}

	public EntityService<?> service;

	public Mirror<? extends HttpCondition> condition;

	protected HttpCondition getCondition(HttpServletRequest request) {
		if (null == condition)
			return null;
		return condition.born(request);
	}

}
