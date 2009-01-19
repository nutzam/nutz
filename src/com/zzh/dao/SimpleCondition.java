package com.zzh.dao;

import com.zzh.dao.entity.Entity;

public class SimpleCondition implements Condition {

	private Object obj;

	public SimpleCondition(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString(Entity<?> entity) {
		return obj.toString();
	}

}
