package org.nutz.dao;

import java.sql.ResultSet;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;

public interface Dao {

	DatabaseMeta meta();

	/**
	 * @return SQLManager interface
	 */
	SqlManager sqls();

	/**
	 * Batch excute a group SQL
	 * 
	 * @param sqls
	 *            the SQLs, which will be executed
	 */
	void execute(Sql... sqls);

	/**
	 * This method try to give client most flexibility. You can do anything in
	 * your ConnCallback implementation and don't need close the connection. In
	 * fact, if you close the connection in your callback, SQLException will be
	 * rised.
	 * 
	 * @param callback
	 *            A ConnCallback Object
	 */
	void run(ConnCallback callback);

	<T> T getObject(Class<T> classOfT, ResultSet rs, FieldMatcher fm);

	<T> T insert(T obj);

	void insert(String tableName, Chain chain);

	void insert(Class<?> classOfT, Chain chain);

	<T> T insertWith(T obj, String regex);

	<T> T insertLinks(T obj, String regex);

	int update(Object obj);

	int update(String tableName, Chain chain, Condition condition);

	int update(Class<?> classOfT, Chain chain, Condition condition);

	<T> T updateWith(T obj, String regex);

	<T> T updateLinks(T obj, String regex);

	void updateRelation(Class<?> classOfT, String regex, Chain chain, Condition condition);

	<T> List<T> query(Class<T> classOfT, Condition condition, Pager pager);

	<T> void delete(Class<T> classOfT, long id);

	<T> void delete(Class<T> classOfT, String name);

	void delete(Object obj);

	<T> void deleteWith(T obj, String regex);

	<T> void deleteLinks(T obj, String regex);

	<T> T fetch(Class<T> classOfT, long id);

	<T> T fetch(Class<T> classOfT, String name);

	<T> T fetch(Class<T> classOfT, Condition condition);

	/**
	 * Fetch one object from DB, it is upon the insert sequence and DB
	 * implementation about which one will be fetched.
	 * 
	 * @param classOfT
	 *            The POJO java type
	 * @return a POJO object, null will be returned if the data set is empty
	 */
	<T> T fetch(Class<T> classOfT);

	<T> T fetch(Entity<T> classOfT, long id);

	<T> T fetch(Entity<T> classOfT, String name);

	<T> T fetch(Entity<T> classOfT, Condition condition);

	<T> T fetch(T obj);

	<T> T fetchLinks(T obj, String regex);

	<T> void clear(Class<T> classOfT, Condition condition);

	<T> void clear(Class<T> classOfT);

	void clear(String tableName, Condition condition);

	void clear(String tableName);

	/**
	 * <pre>
	 * It will delete @One @Many entity records
	 * clear the @ManyMany relations
	 * </pre>
	 * 
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

	int func(Class<?> classOfT, String funcName, String fieldName);
	
	int func(String tableName, String funcName, String fieldName);

	Pager createPager(int pageNumber, int pageSize);

	boolean exists(Class<?> classOfT);

	boolean exists(String tableName);

}
