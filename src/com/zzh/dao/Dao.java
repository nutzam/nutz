package com.zzh.dao;

import java.sql.ResultSet;
import java.util.List;

import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.lang.meta.Pager;

public interface Dao {

	SqlManager sqls();

	SqlMaker maker();

	void execute(Sql<?>... sqls);

	void execute(ConnCallback callback);

	void executeBySqlKey(String... keys);

	<T> T getObject(Class<T> classOfT, ResultSet rs);

	<T> T insert(T obj);

	<T> T insertMany(T obj, String fieldName);

	<T> T insertOne(T obj, String fieldName);

	<T> T update(T obj);

	<T> T update(T obj, boolean ignoreNull);

	<T> T update(T obj, String ignoredFieldsPattern, String activedFieldsPattern);

	<T> List<T> query(Class<T> classOfT, Condition condition, Pager pager);

	void delete(Object obj);

	<T> void delete(Class<T> classOfT, long id);

	<T> void delete(Class<T> classOfT, String name);

	<T> void deleteOne(T obj, String fieldName);

	<T> T fetch(Class<T> classOfT, long id);

	<T> T fetch(Class<T> classOfT, String name);

	<T> T fetchOne(T obj, String fieldName);

	<T> T fetchMany(T obj, String fieldName);

	<T> void clear(Class<T> classOfT, Condition condition);

	<T> void clearMany(T obj, String fieldName);

	<T> Entity<T> getEntity(Class<T> classOfT);

	int count(Class<?> classOfT, Condition condition);

	int count(Class<?> classOfT);

	int getMaxId(Class<?> classOfT);
}
