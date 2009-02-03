package com.zzh.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.zzh.castor.Castors;
import com.zzh.dao.callback.QueryCallback;
import com.zzh.dao.entity.Entity;

class FetchCallback<T> implements QueryCallback<T> {

	private Entity<T> entity;
	private Castors castors;

	public FetchCallback(Entity<T> entity, Castors castors) {
		this.entity = entity;
		this.castors = castors;
	}

	@Override
	public T invoke(ResultSet rs) throws SQLException {
		return entity.getObject(rs, castors);
	}

}
