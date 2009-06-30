package org.nutz.mvc.entity;

import java.lang.reflect.Method;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.mvc.Return;
import org.nutz.service.EntityService;

public class Delete extends EntityAction {

	protected Delete() {
		super();
	}

	public Delete(EntityService<?> service) {
		super(service);
		for (Method m : service.getClass().getMethods()) {
			if (m.getParameterTypes().length == 1 && "delete".equals(m.getName())) {
				Mirror<?> mirror = Mirror.me(m.getParameterTypes()[0]);
				if (mirror.isInteger()) {
					deleteById = m;
				} else if (mirror.isStringLike()) {
					deleteByName = m;
				}
			}
		}
	}

	private Method deleteById;

	private Method deleteByName;

	@Override
	protected Object execute(long id) {
		if (null == deleteById) {
			service.dao().delete(service.getEntityClass(), id);
		} else {
			try {
				deleteById.invoke(service, id);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		return Return.OK();
	}

	@Override
	protected Object execute(String name) {
		if (null == deleteByName) {
			service.dao().delete(service.getEntityClass(), name);
		} else {
			try {
				deleteByName.invoke(service, name);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
		return Return.OK();
	}

}
