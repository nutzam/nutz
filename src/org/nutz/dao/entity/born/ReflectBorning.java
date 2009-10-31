package org.nutz.dao.entity.born;

import java.sql.ResultSet;
import java.util.Iterator;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Borning;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;

abstract class ReflectBorning implements Borning {
	Entity<?> entity;

	ReflectBorning(Entity<?> entity) {
		this.entity = entity;
	}

	abstract Object create() throws Exception;

	public Object born(ResultSet rs, FieldMatcher fm) throws Exception {
		Object obj = create();
		Iterator<EntityField> it = entity.fields().iterator();
		while (it.hasNext()) {
			EntityField ef = it.next();
			if (null == fm || fm.match(ef.getField().getName()))
				ef.fillValue(obj, rs);
		}
		return obj;
	}
}
