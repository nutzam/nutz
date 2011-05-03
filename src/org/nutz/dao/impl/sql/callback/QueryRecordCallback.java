package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlContext;

public class QueryRecordCallback implements SqlCallback {

	public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
		return new ResultSetLooping() {
			protected Object createObject(ResultSet rs, SqlContext context) {
				return Record.create(rs);
			}
		}.doLoop(rs, sql.getContext());
	}

}
