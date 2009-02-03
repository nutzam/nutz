package com.zzh.dao;

public interface SqlManager {

	/**
	 * This method should be thread saft. So each time it will generate a new
	 * SQL instance
	 * 
	 * @param key
	 *            : the sql key
	 * @return SQL object
	 */
	Sql<?> createSql(String key);

	/**
	 * @param classOfT
	 * @param key
	 * @param <T>
	 * @return
	 */
	<S extends Sql<T>, T> S createSql(Class<S> classOfT, String key);

	/**
	 * @param keys
	 * @return
	 */
	ComboSql createComboSQL(String... keys);

	/**
	 * @return the SQL number
	 */
	int count();

	void setPaths(String... paths);

}
