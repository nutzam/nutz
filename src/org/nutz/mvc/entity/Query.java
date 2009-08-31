package org.nutz.mvc.entity;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.dao.Condition;
import org.nutz.dao.Pager;
import org.nutz.dao.QueryResult;
import org.nutz.dao.SimpleCondition;
import org.nutz.lang.Strings;
import org.nutz.mvc.HttpCondition;
import org.nutz.service.EntityService;

public class Query extends ConditionControllor {

	protected Query() {
	}

	public Query(EntityService<?> service) {
		super(service);
	}

	private int pagesize;

	private String orderby;

	private Class<? extends Pager> pagerType;

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Pager pager = Pager.valueOf(request, pagerType == null ? service.dao().getPagerType() : pagerType);
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
		List<?> list = service.query(null == hcnd ? cnd : hcnd, pager);
		return new QueryResult(list, pager);
	}
}
