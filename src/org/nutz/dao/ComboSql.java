package org.nutz.dao;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Strings;

public class ComboSql implements Sql<List<Object>>, Cloneable {

	private List<Sql<?>> sqls = new LinkedList<Sql<?>>();
	private List<Object> result;

	public int count() {
		return sqls.size();
	}

	public ComboSql addSQL(Sql<?> sql) {
		sqls.add(sql);
		return this;
	}

	public ComboSql clone() {
		ComboSql cSql = new ComboSql();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			Sql<?> sql = it.next();
			cSql.sqls.add(sql.born());
		}
		return cSql;
	}

	public ComboSql born() {
		return new ComboSql();
	}

	public List<Object> execute(Connection conn) throws Exception {
		result = new LinkedList<Object>();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			Sql<?> sql = it.next();
			result.add(sql.execute(conn));
		}
		return result;
	}

	public Sql<List<Object>> set(String key, boolean v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, byte v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, double v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, float v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, int v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, long v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, Object v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> set(String key, short v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	public Sql<List<Object>> setValue(Object obj) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().setValue(obj);
		return this;
	}

	public Sql<List<Object>> valueOf(String s) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().valueOf(s);
		return this;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			sb.append(String.format("/*%s*/\n", Strings.dup('-', 40)));
			sb.append(it.next().toString()).append('\n');
		}
		return sb.toString();
	}

	public Object get(String key) {
		if (null == sqls || sqls.size() == 0)
			return null;
		return sqls.get(0).get(key);
	}

	public String toOrginalString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			sb.append(String.format("/*%s*/\n", Strings.dup('-', 40)));
			sb.append(it.next().toOrginalString()).append('\n');
		}
		return sb.toString();
	}

	public List<Object> getResult() {
		return result;
	}

}
