package com.zzh.mvc.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zzh.dao.Condition;
import com.zzh.dao.Pager;
import com.zzh.dao.QueryResult;
import com.zzh.dao.SimpleCondition;
import com.zzh.lang.Strings;
import com.zzh.mvc.HttpCondition;
import com.zzh.service.EntityService;

public class Query<T> extends ConditionControllor<T> {

	public Query(EntityService<T> service) {
		super(service);
	}

	private int pagesize;
	
	private String orderby;
	
	private Class<? extends Pager> pagerType;

	@Override
	public Object execute(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Pager pager = Pager.valueOf(request,pagerType);
		if (pagesize > 0)
			pager.setPageSize(pagesize);
		Condition cnd = null;
		HttpCondition hcnd = getCondition(request);
		if (!Strings.isBlank(orderby)) {
			if (null == hcnd) {
				orderby = Strings.trim(orderby);
				if (orderby.startsWith("ORDER BY"))
					cnd = new SimpleCondition(orderby);
				else
					cnd = new SimpleCondition("ORDER BY " + orderby);
			} else
				hcnd.setOrderBy(orderby);
		}
		List<T> list = service.query(null == hcnd ? cnd : hcnd, pager);
		return new QueryResult(list, pager);
	}
}
