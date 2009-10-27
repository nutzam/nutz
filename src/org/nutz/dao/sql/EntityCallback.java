package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;

public abstract class EntityCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		Entity<?> entity = sql.getEntity();
		if (null == entity)
			throw Lang.makeThrow("SQL without entity : %s", sql.toString());
		return process(rs, entity, sql.getContext().setMatcher(FieldFilter.get(entity.getType())));
	}

	protected abstract Object process(ResultSet rs, Entity<?> entity, SqlContext context)
			throws SQLException;
}
