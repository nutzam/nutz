package org.nutz.dao.sql;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;

public class SqlContext {

	public SqlContext(Entity<?> entity, FieldMatcher matcher) {
		this.entity = entity;
		this.matcher = matcher;
	}

	private Entity<?> entity;
	private FieldMatcher matcher;

	public Entity<?> getEntity() {
		return entity;
	}

	public FieldMatcher getFieldsMatcher() {
		return matcher;
	}

}
