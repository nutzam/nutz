package com.zzh.dao;

import java.sql.ResultSet;
import java.util.List;

import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.entity.Entity;

public interface Dao {

	SqlManager sqls();

	SqlMaker maker();

	void execute(Sql<?>... sqls);

	void execute(ConnCallback callback);

	void executeBySqlKey(String... keys);

	<T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm);

	<T> T insert(T obj);

	<T> T insertWith(T obj, String regex);

	<T> T insertLinks(T obj, String regex);

	<T> T update(T obj);

	<T> T updateWith(T obj, String regex);

	<T> T updateLinks(T obj, String regex);

	<T> List<T> query(Class<T> classOfT, Condition condition, Pager pager);

	<T> void delete(Class<T> classOfT, long id);

	<T> void delete(Class<T> classOfT, String name);

	void delete(Object obj);

	<T> void deleteWith(T obj, String regex);

	<T> void deleteLinks(T obj, String regex);

	<T> T fetch(Class<T> classOfT, long id);
	
	<T> T fetch(Class<T> classOfT, String name);

	<T> T fetch(Class<T> classOfT, Condition condition);
	
	<T> T fetch(Class<T> classOfT);

	<T> T fetch(Entity<T> classOfT, long id);

	<T> T fetch(Entity<T> classOfT, String name);

	<T> T fetch(Entity<T> classOfT, Condition condition);

	<T> T fetch(T obj);

	<T> T fetchLinks(T obj, String regex);

	<T> void clear(Class<T> classOfT, Condition condition);

	void clear(String tableName, Condition condition);

	/**
	 * <pre>
	 * It will delete @One @Many entity records
	 * clear the @ManyMany relations
	 * </pre>
	 * @param obj
	 * @param regex
	 * @return
	 */
	<T> T clearLinks(T obj, String regex);

	<T> Entity<T> getEntity(Class<T> classOfT);

	int count(Class<?> classOfT, Condition condition);

	int count(Class<?> classOfT);

	int count(String tableName, Condition condition);

	int count(String tableName);

	int getMaxId(Class<?> classOfT);

	Pager createPager(int pageNumber, int pageSize);

}
