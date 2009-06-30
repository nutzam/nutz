package org.nutz.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.service.EntityService;

public class Insert extends EntityControllor {

	protected Insert() {
		super();
	}

	public Insert(EntityService<?> service) {
		super(service);
	}

	private String cascade;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Object obj = this.getObject(request);
		service.dao().insertWith(obj, cascade);
		return obj;
	}

}
