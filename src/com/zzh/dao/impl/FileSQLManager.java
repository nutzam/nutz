package com.zzh.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.dao.*;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Strings;
import com.zzh.segment.MultiLineProperties;



public class FileSQLManager implements SQLManager {

	public FileSQLManager() {
	}

	public FileSQLManager(String... paths) {
		this.paths = paths;
	}

	private String[] paths;
	private Map<String, SQL<?>> sqlMaps;

	@Override
	public SQL<?> createSQL(String key) {
		checkSqlMaps(this);
		SQL<?> sql = sqlMaps.get(key);
		if (null != sql)
			return sql.born();
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> SQL<T> createSQL(String key, Class<T> classOfT) {
		return (SQL<T>) createSQL(key);
	}

	@Override
	public int count() {
		checkSqlMaps(this);
		return sqlMaps.size();
	}

	private static void checkSqlMaps(FileSQLManager fsm) {
		if (fsm.sqlMaps == null) {
			synchronized (fsm) {
				if (fsm.sqlMaps == null) {
					fsm.buildSQLMaps();
				}
			}
		}
	}

	private void buildSQLMaps() {
		sqlMaps = new HashMap<String, SQL<?>>();
		for (int i = 0; i < paths.length; i++) {
			File f = Files.findFile(paths[i]);
			try {
				MultiLineProperties p = new MultiLineProperties(new FileInputStream(f));
				Iterator<String> it = p.keySet().iterator();
				while (it.hasNext()) {
					String key = it.next();
					if (sqlMaps.containsKey(key)) {
						throw new RuntimeException("duplicate key '" + key + "'");
					}
					SQL<?> sql = null;
					if (key.charAt(0) == '.') {
						sql = new ExecutableSQL<Object>();
					} else if (key.startsWith("fetch.")) {
						sql = new FetchSQL<Object>();
					} else if (key.startsWith("query.")) {
						sql = new QuerySQL<Object>();
					} else {
						throw new RuntimeException("SQL wrong key syntax: '" + key + "'");
					}
					sql.valueOf(Strings.trim(p.get(key)));
					sqlMaps.put(key, sql);
				}

			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
