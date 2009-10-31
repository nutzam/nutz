package org.nutz.dao.impl;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.DaoUtils;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Link;

class ManyManyCondition implements Condition {

	private Dao dao;
	private Link link;
	private Object obj;

	ManyManyCondition(Dao dao, Link link, Object obj) {
		this.dao = dao;
		this.link = link;
		this.obj = obj;
	}

	public String toSql(Entity<?> me) {
		return String.format("%s IN (SELECT %s FROM %s WHERE %s=%s)", dao.getEntity(
				link.getTargetClass()).getField(link.getTargetField().getName()).getColumnName(),
				link.getTo(), link.getRelation(), link.getFrom(), evalValue(me));
	}

	private Object evalValue(Entity<?> me) {
		return DaoUtils.formatFieldValue(me.getMirror().getValue(obj, link.getReferField()));
	}

}
