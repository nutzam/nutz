package org.nutz.dao.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.nutz.dao.Condition;
import org.nutz.lang.segment.Segment;

public interface Sql extends Segment {

	Sql setContext(SqlContext context);

	Sql setCallback(SqlCallback callback);

	Sql setCondition(Condition condition);

	void execute(Connection conn) throws SQLException;
	
	Sql born();
	
	Sql clone();

	Object getResult();
}
