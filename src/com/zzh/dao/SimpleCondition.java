package com.zzh.dao;

import com.zzh.dao.entity.Entity;

public class SimpleCondition implements Condition {

	private String content;

	public SimpleCondition(Object obj) {
		this.content = obj.toString();
	}

	public SimpleCondition(String format, Object... args) {
		this.content = String.format(format, args);
	}

	@Override
	public String toString(Entity<?> entity) {
		return content;
	}

}
