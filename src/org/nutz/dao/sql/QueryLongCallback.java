package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.lang.util.LinkedLongArray;

public class QueryLongCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		LinkedLongArray ary = new LinkedLongArray(20);
		while (rs.next())
			ary.push(rs.getLong(1));
		return ary.toArray();
	}

}
