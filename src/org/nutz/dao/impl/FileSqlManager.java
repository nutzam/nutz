package org.nutz.dao.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.resource.Scans;

public class FileSqlManager extends AbstractSqlManager {

	private String[] paths;

	private String regex;

	public FileSqlManager(String... paths) {
		this.paths = paths;
	}

	public String getRegex() {
		return regex;
	}

	public FileSqlManager setRegex(String regex) {
		this.regex = regex;
		return this;
	}

	public void refresh() {
		List<InputStream> list = Scans.me().loadResource(regex, paths);
		_sql_map = new HashMap<String, String>();
		for (InputStream ins : list) {
			Reader r = new InputStreamReader(ins);
			try {
				loadSQL(r);
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		}
	}

}
