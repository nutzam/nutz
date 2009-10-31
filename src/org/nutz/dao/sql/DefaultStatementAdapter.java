package org.nutz.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class DefaultStatementAdapter implements StatementAdapter {

	public static DefaultStatementAdapter ME = new DefaultStatementAdapter();

	public void process(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		if (null == entity)
			processWithoutEntity(stat, sql);
		else
			processWithEntity(stat, sql, entity);
	}

	private void processWithoutEntity(PreparedStatement stat, SqlLiteral sql) throws SQLException {
		for (String name : sql.getParams().keys()) {
			Object obj = sql.getParams().get(name);
			int[] is = sql.getParamIndexes(name);
			if (null == is || is.length == 0)
				continue;
			FieldAdapter fss = FieldAdapter.create(null == obj ? null : Mirror.me(obj.getClass()),
					null);
			fss.set(stat, obj, is);
		}
	}

	private void processWithEntity(PreparedStatement stat, SqlLiteral sql, Entity<?> entity)
			throws SQLException {
		for (EntityField ef : entity.fields()) {
			String name = ef.getField().getName();
			Object obj = sql.getParams().get(name);
			int[] is = sql.getParamIndexes(name);
			if (null == is || is.length == 0)
				continue;
			if (ef == null)
				continue;
			if (null == obj) {
				if (null != ef)
					if (ef.isNotNull())
						throw Lang.makeThrow("Field %s(%s).%s(%s) can not be NULL.", entity
								.getType().getName(), entity.getTableName(), ef.getField()
								.getName(), ef.getColumnName());
			}
			ef.getFieldAdapter().set(stat, obj, is);
		}

	}

}
