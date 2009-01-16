package com.zzh.dao;

import java.sql.ResultSet;
import java.util.List;

import com.zzh.dao.callback.ConnCallback;
import com.zzh.dao.entity.Entity;
import com.zzh.lang.meta.Pager;


public interface Dao {
	/**
	 * @return SQLMaker interface
	 */
	SQLMaker maker();

	/**
	 * @return SQLManager object
	 */
	SQLManager sqls();

	/**
	 * Execute one group SQL in same transaction
	 * 
	 * @param sqls
	 */
	void execute(SQL<?>... sqls);

	/**
	 * Execute by ConnCallback object
	 * 
	 * @param callback
	 */
	void execute(ConnCallback callback);

	/**
	 * @param key
	 *            : the SQL key existed in configuration file
	 */
	void executeBySqlKey(String... keys);

	/**
	 * Get Entity object from ResultSet object
	 * 
	 * @param rs
	 * @return POJO Object
	 */
	<T> T getObject(Class<T> classOfT, ResultSet rs);

	/**
	 * Insert single object
	 * 
	 * @param obj
	 * @return
	 */
	<T> T insert(T obj);

	/**
	 * Update one object
	 * 
	 * @param obj
	 * @return
	 */
	<T> T update(T obj);

	/**
	 * @param classOfT
	 * @param condition
	 * @param pager
	 * 
	 * @return a group Object
	 */
	<T> List<T> query(Class<T> classOfT, Condition condition, Pager pager);

	/**
	 * Delete one object
	 * 
	 * @param classOfT
	 * @param id
	 */
	<T> void delete(Class<T> classOfT, long id);

	/**
	 * Delete one object by its name
	 * 
	 * @param classOfT
	 * @param name
	 */
	<T> void delete(Class<T> classOfT, String name);

	/**
	 * Fetch one object by its ID
	 * 
	 * @param classOfT
	 * @param id
	 * @return
	 */
	<T> T fetch(Class<T> classOfT, long id);

	/**
	 * Delete one object by it's name.
	 * 
	 * @param classOfT
	 * @param name
	 * @return
	 */
	<T> T fetch(Class<T> classOfT, String name);

	/**
	 * Clear one kinds of object from DB
	 * 
	 * @param classOfT
	 * @param condition
	 */
	<T> void clear(Class<T> classOfT, Condition condition);

	/**
	 * @param classOfT
	 * @param condition
	 * @param pager
	 * @return a group Object
	 */

	/**
	 * @param classOfT
	 *            : entity class
	 * @return ResultSetEntityMapping object
	 */
	<T> Entity<T> getEntity(Class<T> classOfT);

	/**
	 * Count special records number
	 * 
	 * @param classOfT
	 * @param condition
	 * @return record number
	 */
	<T> int count(Class<T> classOfT, Condition condition);

	/**
	 * @param classOfT
	 * @return
	 */
	<T> int getMaxId(Class<T> classOfT);
}
