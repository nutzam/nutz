package org.nutz.dao.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateSql extends AbstractSql {

	UpdateSql(SqlLiteral sql) {
		super(sql);
	}

	private int updateCount;

	public int getUpdateCount() {
		return updateCount;
	}

	@Override
	public void process(PreparedStatement stat) throws SQLException {
		updateCount = stat.executeUpdate();
	}

}
