package org.nutz.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ExecutableSql extends AbstractSql {

	ExecutableSql(SqlLiteral sql) {
		super(sql);
	}

	@Override
	public void process(PreparedStatement stat) throws SQLException {
		stat.execute();
	}

}
