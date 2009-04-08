package com.zzh.dao.impl;

import java.sql.ResultSet;

import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;

class FetchCallback<T> extends QueryCallback<T> {

	private Entity<T> entity;

	public FetchCallback(Entity<T> entity) {
		this.entity = entity;
	}

	@Override
	public T invoke(ResultSet rs){
		return entity.getObject(rs,this.getMatcher());
	}

}
