package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.Dao;
import org.nutz.dao.callback.ConnCallback;

public class ExecutableSql extends AbstractSql {

	@Override
	public Sql born() {
		return new ExecutableSql();
	}

	@Override
	public void execute(Connection conn) throws SQLException {
		Statement stat = conn.createStatement();

	}

}
