package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class QueryStringCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		List<String> list = new LinkedList<String>();
		while (rs.next())
			list.add(rs.getString(1));
		return list.toArray(new String[list.size()]);
	}

}
