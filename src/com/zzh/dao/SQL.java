package com.zzh.dao;

import java.sql.Connection;

import com.zzh.segment.Segment;


public interface SQL<T> extends Segment {

	T execute(Connection conn) throws Exception;

	int getIndex(String key);

	SQL<T> setValue(Object obj);

	SQL<T> born();

	SQL<T> clone();

}
