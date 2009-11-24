package org.nutz.dao.impl;

import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;

public class ManyCondition implements Condition {

	private Object value;
	private Link link;

	public ManyCondition(Link link, Object value) {
		this.link = link;
		this.value = value;
	}

	public String toSql(Entity<?> entity) {
		return String.format("%s=%s", entity.getField(link.getTargetField().getName())
				.getColumnName(), Sqls.formatFieldValue(value));
	}

}
