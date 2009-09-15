package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QuerySql extends AbstractSql {

	QuerySql(SqlLiteral sql) {
		super(sql);
	}

	@Override
	public void process(PreparedStatement stat) throws SQLException {}

	

}
