package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlContext;

public class PojoQueryRecordCallback implements PojoCallback {

	public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
		return new ResultSetLooping() {
			protected Object createObject(ResultSet rs, SqlContext context) {
				return Record.create(rs);
			}
		}.doLoop(rs, pojo.getContext());
	}

}
