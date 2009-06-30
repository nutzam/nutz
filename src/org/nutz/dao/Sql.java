package org.nutz.dao;

import java.sql.Connection;

public interface Sql<T> {

	Sql<T> valueOf(String s);
	
	T execute(Connection conn) throws Exception;

	Sql<T> setValue(Object obj);

	Sql<T> born();

	Sql<T> clone();

	Sql<T> set(String key, boolean v);

	Sql<T> set(String key, int v);

	Sql<T> set(String key, double v);

	Sql<T> set(String key, float v);

	Sql<T> set(String key, long v);

	Sql<T> set(String key, byte v);

	Sql<T> set(String key, short v);

	Sql<T> set(String key, Object v);

	Object get(String key);
	
	String toOrginalString();

	T getResult();

}
