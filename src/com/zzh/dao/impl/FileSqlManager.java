package com.zzh.dao.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zzh.castor.Castors;
import com.zzh.dao.*;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.segment.MultiLineProperties;

public class FileSqlManager implements SqlManager {

	public FileSqlManager() {
		this.castors = Castors.me();
	}

	public FileSqlManager(String... paths) {
		this();
		this.paths = paths;
	}

	private Castors castors;
	private String[] paths;
	private Map<String, Sql<?>> sqlMaps;

	public void setCastors(Castors castors) {
		this.castors = castors;
	}

	public void setPaths(String... paths) {
		this.paths = paths;
		this.sqlMaps = null;
	}

	@Override
	public Sql<?> createSql(String key) {
		checkSqlMaps(this);
		Sql<?> sql = sqlMaps.get(key);
		if (null == sql)
			throw new SqlNotFoundException(key, paths);
		return sql.born();
	}

	@Override
	public ComboSql createComboSQL(String... keys) {
		checkSqlMaps(this);
		ComboSql combo = new ComboSql();
		if (null == keys || keys.length == 0) {
			for (Iterator<Sql<?>> it = sqlMaps.values().iterator(); it.hasNext();) {
				combo.addSQL(it.next());
			}
		} else
			for (String key : keys) {
				Sql<?> sql = createSql(key);
				combo.addSQL(sql);
			}
		return combo;
	}

	@Override
	public <S extends Sql<T>, T> S createSql(Class<S> classOfT, String key) {
		checkSqlMaps(this);
		Sql<?> sql = sqlMaps.get(key);
		if (null == sql)
			throw new SqlNotFoundException(key, paths);
		S re = Mirror.me(classOfT).born(castors);
		re.valueOf(sql.toOrginalString());
		return re;
	}

	@Override
	public int count() {
		checkSqlMaps(this);
		return sqlMaps.size();
	}

	private static void checkSqlMaps(FileSqlManager fsm) {
		if (fsm.sqlMaps == null) {
			synchronized (fsm) {
				if (fsm.sqlMaps == null) {
					fsm.buildSQLMaps();
				}
			}
		}
	}

	private void buildSQLMaps() {
		sqlMaps = new HashMap<String, Sql<?>>();
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
					Sql<?> sql = null;
					if (key.charAt(0) == '.') {
						sql = new ExecutableSql<Object>(castors);
					} else if (key.startsWith("fetch.")) {
						sql = new FetchSql<Object>(castors);
					} else if (key.startsWith("query.")) {
						sql = new QuerySql<Object>(castors);
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
