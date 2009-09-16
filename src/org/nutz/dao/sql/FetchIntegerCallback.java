package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchIntegerCallback implements SqlCallback {

	public void invoke(Connection conn, PreparedStatement stat, Sql sql) throws SQLException {
		ResultSet rs = stat.getResultSet();
		if (rs.first())
			sql.setResult(rs.getInt(1));

	}

}
