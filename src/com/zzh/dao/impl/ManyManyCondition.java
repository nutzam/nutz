package com.zzh.dao.impl;

import com.zzh.dao.Condition;
import com.zzh.dao.Dao;
import com.zzh.dao.Sqls;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.Link;

class ManyManyCondition implements Condition {

	private Dao dao;
	private Link link;
	private Object obj;

	ManyManyCondition(Dao dao, Link link, Object obj) {
		this.dao = dao;
		this.link = link;
		this.obj = obj;
	}

	@Override
	public String toString(Entity<?> me) {
		return String.format("%s IN (SELECT %s FROM %s WHERE %s=%s)", dao.getEntity(
				link.getTargetClass()).getField(link.getTargetField().getName()).getColumnName(),
				link.getTo(), link.getRelation(), link.getFrom(), evalValue(me));
	}

	private Object evalValue(Entity<?> me) {
		return Sqls.formatFieldValue(me.getMirror().getValue(obj, link.getReferField()));
	}

}
