package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.EntityService;

public class Insert<T> extends EntityControllor<T> {

	public Insert(EntityService<T> service) {
		super(service);
	}

	private String cascade;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		T obj = this.getObject(request);
		service.dao().insertWith(obj,cascade);
		return obj;
	}

}
