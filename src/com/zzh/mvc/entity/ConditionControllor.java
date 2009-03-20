package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;

import com.zzh.lang.Mirror;
import com.zzh.mvc.Controllor;
import com.zzh.mvc.HttpCondition;
import com.zzh.service.EntityService;

public abstract class ConditionControllor<T> implements Controllor {

	protected ConditionControllor() {
	}

	protected ConditionControllor(EntityService<T> service) {
		this();
		this.service = service;
	}

	public EntityService<T> service;

	public Mirror<? extends HttpCondition> condition;

	protected HttpCondition getCondition(HttpServletRequest request) {
		if (null == condition)
			return null;
		return condition.born(request);
	}

}
