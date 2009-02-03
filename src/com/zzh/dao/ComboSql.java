package com.zzh.dao;

import java.sql.Connection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ComboSql implements Sql<Object> {

	private List<Sql<?>> sqls = new LinkedList<Sql<?>>();

	public ComboSql addSQL(Sql<?> sql) {
		sqls.add(sql);
		return this;
	}

	@Override
	public Sql<Object> clone() {
		ComboSql cSql = new ComboSql();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			Sql<?> sql = it.next();
			cSql.sqls.add(sql.born());
		}
		return cSql;
	}

	@Override
	public Sql<Object> born() {
		return new ComboSql();
	}

	@Override
	public Object execute(Connection conn) throws Exception {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();) {
			it.next().execute(conn);
		}
		return null;
	}

	@Override
	public Sql<Object> set(String key, boolean v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, byte v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, double v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, float v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, int v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, long v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, Object v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> set(String key, short v) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().set(key, v);
		return this;
	}

	@Override
	public Sql<Object> setValue(Object obj) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().setValue(obj);
		return this;
	}

	@Override
	public Sql<Object> valueOf(String s) {
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			it.next().valueOf(s);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Sql<?>> it = sqls.iterator(); it.hasNext();)
			sb.append(it.next().toString());
		return sb.toString();
	}

	@Override
	public Object get(String key) {
		if (null == sqls || sqls.size() == 0)
			return null;
		return sqls.get(0).get(key);
	}

	@Override
	public String toOrginalString() {
		return null;
	}

}
