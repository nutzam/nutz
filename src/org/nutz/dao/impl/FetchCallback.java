package org.nutz.dao.impl;

import java.sql.ResultSet;

import org.nutz.dao.callback.QueryCallback;
import org.nutz.dao.entity.Entity;

public class FetchCallback<T> extends QueryCallback<T> {

	private Entity<T> entity;

	public FetchCallback(Entity<T> entity) {
		this.entity = entity;
	}

	@Override
	public T invoke(ResultSet rs){
		return entity.getObject(rs,this.getContext().getFieldsMatcher());
	}

}
