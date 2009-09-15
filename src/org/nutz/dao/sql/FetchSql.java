package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

public class FetchSql extends AbstractSql {

	public Sql born() {
		return new FetchSql();
	}

	public void execute(Connection conn) throws SQLException {
		
	}

}
