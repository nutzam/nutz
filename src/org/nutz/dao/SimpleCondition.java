package org.nutz.dao;

import org.nutz.dao.entity.Entity;

public class SimpleCondition implements Condition {

	private String content;

	public SimpleCondition(Object obj) {
		this.content = obj.toString();
	}

	public SimpleCondition(String format, Object... args) {
		this.content = String.format(format, args);
	}

	public String toSql(Entity<?> entity) {
		return content;
	}

}
