package org.nutz.mvc.entity;

import org.nutz.lang.Strings;
import org.nutz.mvc.Action;
import org.nutz.mvc.Return;
import org.nutz.mvc.annotation.Param;
import org.nutz.service.EntityService;

public abstract class EntityAction extends Action {

	protected EntityAction() {
		super();
	}

	protected EntityAction(EntityService<?> service) {
		this();
		this.service = service;

	}

	protected EntityService<?> service;
	
	@Param
	private long id;

	@Param
	private String name;

	@Override
	public Object execute() throws Exception {
		try {
			if (!Strings.isEmpty(name))
				return execute(name);
			else
				return execute(id);
		} catch (Throwable e) {
			return Return.fail("Fail to %s '%s' by '%s', for the reason: %s", this.getClass()
					.getSimpleName(), service.getEntityClass().getName(),
					(Strings.isEmpty(name) ? id : name), e.getMessage());
		}
	}

	protected abstract Object execute(long id);

	protected abstract Object execute(String name);

}
