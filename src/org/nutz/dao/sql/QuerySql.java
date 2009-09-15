package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class QuerySql extends AbstractSql {

	public Sql born() {
		return new QuerySql();
	}

	public void execute(Connection conn) throws SQLException {}

}
