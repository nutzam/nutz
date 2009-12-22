package org.nutz.dao.sql;

import java.sql.Connection;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;

public class ComboSql implements Sql {

	public ComboSql() {
		sqls = new LinkedList<Sql>();
		varss = new ComboVarSet();
		holderss = new ComboVarSet();
	}

	private List<Sql> sqls;
	private ComboVarSet varss;
	private ComboVarSet holderss;

	public int count() {
		return sqls.size();
	}

	public ComboSql add(Sql sql) {
		sqls.add(sql);
		varss.add(sql.vars());
		holderss.add(sql.params());
		return this;
	}

	public VarIndex paramIndex() {
		throw Lang.noImplement();
	}

	public VarIndex varIndex() {
		throw Lang.noImplement();
	}

	public ComboSql clear() {
		sqls.clear();
		varss.clear();
		holderss.clear();
		return this;
	}

	public Sql duplicate() {
		ComboSql re = new ComboSql();
		for (Sql sql : sqls)
			re.add(sql.duplicate());
		return re;
	}

	public void execute(Connection conn) {
		if (sqls.isEmpty())
			return;
		try {
			boolean old = conn.getAutoCommit();
			try {
				conn.setAutoCommit(false);
				for (Sql sql : sqls)
					if (null != sql)
						sql.execute(conn);
				if (old)
					conn.commit();
			} finally {
				conn.setAutoCommit(old);
			}
		} catch (SQLException e) {
			throw new DaoException(e);
		}
	}

	public SqlContext getContext() {
		if (sqls.isEmpty())
			return null;
		return sqls.get(0).getContext();
	}

	public Object getResult() {
		List<Object> list = new ArrayList<Object>(sqls.size());
		for (Sql sql : sqls)
			list.add(sql.getResult());
		return list;
	}

	public int getUpdateCount() {
		int re = -1;
		for (Sql sql : sqls) {
			if (sql.getUpdateCount() != -1) {
				if (re == -1)
					re = 0;
				re += sql.getUpdateCount();
			}
		}
		return re;
	}

	public VarSet params() {
		return holderss;
	}

	public Sql setAdapter(StatementAdapter adapter) {
		for (Sql sql : sqls)
			sql.setAdapter(adapter);
		return this;
	}

	public Sql setCallback(SqlCallback callback) {
		for (Sql sql : sqls)
			sql.setCallback(callback);
		return this;
	}

	public Sql setCondition(Condition condition) {
		for (Sql sql : sqls)
			sql.setCondition(condition);
		return this;
	}

	public Sql setEntity(Entity<?> entity) {
		for (Sql sql : sqls)
			sql.setEntity(entity);
		return this;
	}

	public VarSet vars() {
		return null;
	}

	public Entity<?> getEntity() {
		if (sqls.isEmpty())
			return null;
		return sqls.get(0).getEntity();
	}

	public int getInt() {
		throw Lang.makeThrow("Not implement yet!");
	}

	public <T> List<T> getList(Class<T> classOfT) {
		throw Lang.makeThrow("Not implement yet!");
	}

	public <T> T getObject(Class<T> classOfT) {
		throw Lang.makeThrow("Not implement yet!");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Sql sql : sqls)
			sb.append(sql.toString()).append('\n');
		return sb.toString();
	}
}
