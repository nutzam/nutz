package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchLongCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		if (rs.next())
			return rs.getLong(1);
		return null;
	}

}
