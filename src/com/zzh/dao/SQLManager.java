package com.zzh.dao;

public interface SQLManager {

	/**
	 * This method should be thread saft. So each time it will generate a new
	 * SQL instance
	 * 
	 * @param key
	 *            : the sql key
	 * @return SQL object
	 */
	SQL<?> createSQL(String key);

	/**
	 * @param <T>
	 * @param key
	 * @param classOfT
	 * @return
	 */
	<T> SQL<T> createSQL(String key, Class<T> classOfT);

	/**
	 * @return the SQL number
	 */
	int count();

}

