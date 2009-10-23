package org.nutz.dao.entity.born;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Borning;

class FMResultSetConstructorBorning implements Borning {

	Constructor<?> c;

	FMResultSetConstructorBorning(Constructor<?> c) {
		this.c = c;
	}

	public Object born(ResultSet rs, FieldMatcher fm) throws Exception {
		return c.newInstance(rs, fm);
	}

}