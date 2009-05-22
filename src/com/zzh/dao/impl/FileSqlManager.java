package com.zzh.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zzh.dao.*;
import com.zzh.lang.Files;
import com.zzh.lang.Lang;
import com.zzh.lang.Mirror;
import com.zzh.lang.Strings;
import com.zzh.lang.util.LinkedCharArray;

public class FileSqlManager implements SqlManager {

	private final static FilenameFilter defaultSqkFileFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.endsWith(".sqls");
		}
	};

	public FileSqlManager(String... paths) {
		this.paths = paths;
	}

	private String[] paths;
	private Map<String, Sql<?>> sqlMaps;
	private String[] keys;
	private FilenameFilter sqkFileFilter;

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
	public ComboSql createComboSql(String... keys) {
		checkSqlMaps(this);
		ComboSql combo = new ComboSql();
		for (String key : (null == keys || keys.length == 0 ? this.keys : keys)) {
			Sql<?> sql = createSql(key);
			combo.addSQL(sql);
		}
		return combo;
	}

	@Override
	public <S extends Sql<?>> S createSql(Class<S> classOfT, String key) {
		checkSqlMaps(this);
		Sql<?> sql = sqlMaps.get(key);
		if (null == sql)
			throw new SqlNotFoundException(key, paths);
		S re = Mirror.me(classOfT).born();
		re.valueOf(sql.toOrginalString());
		return re;
	}

	@Override
	public int count() {
		checkSqlMaps(this);
		return sqlMaps.size();
	}

	@Override
	public String[] keys() {
		checkSqlMaps(this);
		return keys;
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
		if (null != paths)
			for (String path : paths) {
				if (null == path)
					continue;
				File f = Files.findFile(Strings.trim(path));
				if (f == null)
					throw Lang.makeThrow("Can not find file [%s]", Strings.trim(path));
				File[] files;
				if (f.isDirectory()) {
					files = f.listFiles(sqkFileFilter == null ? defaultSqkFileFilter
							: sqkFileFilter);
				} else
					files = Lang.array(f);
				try {
					for (File file : files) {
						SqlFileBuilder p = new SqlFileBuilder(new BufferedReader(
								new InputStreamReader(new FileInputStream(file), "UTF-8")));
						Iterator<String> it = p.keys().iterator();
						keys = new String[p.map.size()];
						int i = 0;
						while (it.hasNext()) {
							String key = it.next();
							String value = Strings.trim(p.get(key));
							if (sqlMaps.containsKey(key)) {
								throw Lang.makeThrow("duplicate key '%s'", key);
							}
							Sql<?> sql = null;
							if (key.startsWith("fetch:")) {
								sql = new FetchSql<Object>();
								key = key.substring("fetch:".length());
							} else if (key.startsWith("query:")) {
								sql = new QuerySql<Object>();
								key = key.substring("query:".length());
							} else {
								sql = new ExecutableSql();
							}
							key = Strings.trim(key);
							sql.valueOf(value);
							sqlMaps.put(key, sql);
							keys[i++] = key;
						}
					}
				} catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
	}

	static final Pattern ptn = Pattern.compile("(?<=^\n/[*])(.*)(?=[*]/)");

	static class InnerStack {

		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		LinkedCharArray list = new LinkedCharArray();
		LinkedCharArray cmts = new LinkedCharArray();
		String key = null;
		boolean inNormalComment;

		void eat(int c) {
			if (inNormalComment) {
				if (cmts.push(c).endsWith("*/")) {
					cmts.clear();
					inNormalComment = false;
				}
			} else if (key != null) {
				if (list.push(c).endsWith("\n/*")) {
					list.popLast(3);
					addOne();
					list.push("\n/*");
				} else if (list.endsWith("/*")) {
					list.popLast(2);
					inNormalComment = true;
				}
			} else {
				if (list.size() < 3) {
					if (!"\n/*".startsWith(list.push(c).toString())) {
						list.clear();
					}
				} else {
					if (list.push(c).endsWith("*/")) {
						Matcher matcher = ptn.matcher(list.clear());
						if (matcher.find()) {
							key = Strings.trim(matcher.group());
						}
					}
				}
			}
		}

		void addOne() {
			String value = Strings.trim(list.clear());
			if (!Strings.isBlank(value))
				map.put(key, value);
			key = null;
		}

	}

	static class SqlFileBuilder {
		LinkedHashMap<String, String> map;

		SqlFileBuilder(BufferedReader reader) throws IOException {
			InnerStack stack = new InnerStack();
			int c;
			stack.eat('\n');
			while (-1 != (c = reader.read())) {
				stack.eat(c);
			}
			if (stack.key != null)
				stack.addOne();
			map = stack.map;
		}

		Set<String> keys() {
			return map.keySet();
		}

		String get(String key) {
			return map.get(key);
		}
	}

}
