package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.lang.util.LinkedIntArray;

public class QueryIntCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		LinkedIntArray ary = new LinkedIntArray(20);
		while (rs.next())
			ary.push(rs.getInt(1));
		return ary.toArray();
	}

}
