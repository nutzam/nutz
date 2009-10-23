package org.nutz.dao.entity.born;

import java.lang.reflect.Method;

import org.nutz.dao.entity.Entity;

class DefaultStaticMethodBorning extends ReflectBorning {

	Method method;

	DefaultStaticMethodBorning(Entity<?> entity, Method defMethod) {
		super(entity);
		this.method = defMethod;
	}

	public Object create() throws Exception {
		return method.invoke(null);
	}
}
