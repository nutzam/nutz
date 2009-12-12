package org.nutz.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class DefaultStatementAdapter implements StatementAdapter {

	public final static DefaultStatementAdapter ME = new DefaultStatementAdapter();

	public void process(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		if (null == entity)
			processWithoutEntity(stat, sql);
		else
			processWithEntity(stat, sql, entity);
	}

	private void processWithoutEntity(PreparedStatement stat, SqlLiteral sql) throws SQLException {
		for (String name : sql.getParamIndexes().names()) {
			Object obj = sql.getParams().get(name);
			int[] is = sql.getParamIndexes(name);
			if (null == is || is.length == 0)
				continue;
			FieldAdapter.create(Mirror.me(obj), false).set(stat, obj, is);
		}
	}

	private void processWithEntity(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		// for (EntityField ef : entity.fields()) {
		for (String name : sql.getParamIndexes().names()) {
			int[] is = sql.getParamIndexes(name);
			if (null == is || is.length == 0)
				continue;
			EntityField ef = entity.getField(name);
			Object obj = sql.getParams().get(name);
			// Find one entity field match with the param
			if (null != ef) {
				if (null == obj) {
					if (null != ef)
						if (ef.isNotNull())
							throw Lang.makeThrow("Field %s(%s).%s(%s) can not be NULL.", entity
									.getType().getName(), entity.getTableName(), ef.getField()
									.getName(), ef.getColumnName());
				}
				ef.getFieldAdapter().set(stat, obj, is);
			}
			// Try to get from params
			else {
				FieldAdapter.create(Mirror.me(obj), false).set(stat, obj, is);
			}
		}

	}

}
