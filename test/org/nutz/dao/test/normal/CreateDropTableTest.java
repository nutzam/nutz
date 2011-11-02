package org.nutz.dao.test.normal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.test.DaoCase;

public class CreateDropTableTest extends DaoCase {

	private static String DESC_TABLE_SQL = "select column_name,is_nullable,column_default from information_schema.`columns` where table_name = @tableName and column_name = @columnName";

	@Test
	public void mysqlCreateTableTimestampFieldDefaultNull() throws Exception {
		boolean isMySql = dao.meta().isMySql();
		// 这个仅仅测试MySQL数据库
		if (isMySql) {
			dao.create(TableWithTimestampInMySql.class, true);
			Sql descTable = Sqls.create(DESC_TABLE_SQL);
			descTable.params().set("tableName", "t_ts").set("columnName", "ct");
			descTable.setCallback(new SqlCallback() {
				@Override
				public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
					boolean defaultIsNull = false;
					while (rs.next()) {
						String columnName = rs.getString(1);
						String defaultValue = rs.getString(3);
						// ct字段
						if ("ct".equals(columnName)) {
							if (null == defaultValue || "NULL".equals(defaultValue.toUpperCase())) {
								defaultIsNull = true;
							}
						}
					}
					return defaultIsNull;
				}
			});
			dao.execute(descTable);
			boolean defaultIsNull = descTable.getObject(Boolean.class);
			Assert.assertTrue(defaultIsNull);
		}
	}
}
