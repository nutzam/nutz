package org.nutz.mvc.entity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.mvc.Return;
import org.nutz.service.EntityService;

public class Clear extends ConditionControllor {

	protected Clear() {
		super();
	}

	public Clear(EntityService<?> service) {
		super(service);
	}

	public Object execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			service.clear(getCondition(request));
			return Return.OK();
		} catch (Exception e) {
			return Return.fail("Fail to clear '%s' by '%s', for the reason: %s", service.getEntityClass().getName(),
					getCondition(request).toString(service.getEntity()), e.getMessage());
		}
	}

}
