package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;

public abstract class EntityCallback implements SqlCallback {

	public void invoke(Connection conn, PreparedStatement stat, Sql sql) throws SQLException {
		Entity<?> entity = sql.getEntity();
		if (null == entity)
			Lang.makeThrow("SQL without entity : %s", sql.toString());
		Object re = process(stat.getResultSet(), entity, sql.getContext());
		sql.setResult(re);
	}

	protected abstract Object process(ResultSet rs, Entity<?> entity, SqlContext context) throws SQLException;
}
