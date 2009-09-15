package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FetchSql extends AbstractSql {

	FetchSql(SqlLiteral sql) {
		super(sql);
	}

	@Override
	public void process(PreparedStatement stat) throws SQLException {
		
	}

}
