package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.service.EntityService;

public class ClearEntities extends ConditionControllor<EntityService<?>> {

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try {
			this.getService().clear(getCondition(request));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
