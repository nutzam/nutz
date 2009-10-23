package org.nutz.dao.entity.born;

import java.lang.reflect.Method;
import java.sql.ResultSet;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Borning;

class StaticResultSetMethodBorning implements Borning {

	Method method;

	StaticResultSetMethodBorning(Method rsMethod) {
		this.method = rsMethod;
	}

	public Object born(ResultSet rs, FieldMatcher fm) throws Exception {
		return method.invoke(null, rs);
	}

}
