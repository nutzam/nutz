package com.zzh.mvc.c;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.lang.meta.Pager;
import com.zzh.lang.meta.QueryResult;
import com.zzh.service.EntityService;

public class QueryEntities extends ConditionControllor<EntityService<?>> {

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Pager pager = Pager.valueOf(request);
		return new QueryResult(getService().query(getCondition(request), pager), pager);
	}

}
