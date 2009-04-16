package com.zzh.dao.impl;

import com.zzh.dao.Condition;
import com.zzh.dao.Sqls;
import com.zzh.dao.entity.Entity;
import com.zzh.dao.entity.Link;

public class ManyCondition implements Condition {

	private Object value;
	private Link link;

	public ManyCondition(Link link, Object value) {
		this.link = link;
		this.value = value;
	}

	@Override
	public String toString(Entity<?> entity) {
		return String.format("%s=%s", entity.getField(link.getTargetField().getName())
				.getColumnName(), Sqls.formatFieldValue(value));
	}

}
