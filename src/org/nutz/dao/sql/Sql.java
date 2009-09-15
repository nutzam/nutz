package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.nutz.dao.Condition;

public interface Sql {

	VarSet vars();

	VarSet holders();

	SqlContext getContext();

	Sql setContext(SqlContext context);

	Sql setCallback(SqlCallback callback);

	Sql setCondition(Condition condition);

	void execute(Connection conn) throws SQLException;

	Sql clone();

	Object getResult();
}
