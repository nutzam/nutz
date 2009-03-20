package com.zzh.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.mvc.Return;
import com.zzh.service.EntityService;

public class Clear<T> extends ConditionControllor<T> {

	public Clear(EntityService<T> service) {
		super(service);
	}

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			service.clear(getCondition(request));
			return Return.OK();
		} catch (Exception e) {
			return Return.fail("Fail to clear '%s' by '%s', for the reason: %s", service
					.getEntityClass().getName(), getCondition(request)
					.toString(service.getEntity()), e.getMessage());
		}
	}

}
