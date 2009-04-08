package com.zzh.dao.callback;

import java.sql.ResultSet;

import com.zzh.dao.FieldMatcher;

public abstract class QueryCallback<T> {

	private FieldMatcher matcher;

	public FieldMatcher getMatcher() {
		return matcher;
	}

	public void setMatcher(FieldMatcher actived) {
		this.matcher = actived;
	}

	public abstract T invoke(ResultSet rs) throws Exception;

}
