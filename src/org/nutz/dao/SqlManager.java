package org.nutz.dao;

import org.nutz.dao.sql.ComboSql;
import org.nutz.dao.sql.Sql;

public interface SqlManager {

	String get(String key) throws SqlNotFoundException;

	Sql create(String key) throws SqlNotFoundException;

	/**
	 * @param keys
	 * @return
	 */
	ComboSql createCombo(String... keys);

	/**
	 * @return the SQL number
	 */
	int count();

	void setPaths(String... paths);

	/**
	 * @return
	 */
	String[] keys();

	void refresh();

	void addSql(String key, String value);

	void remove(String key);

	void clear();

}
