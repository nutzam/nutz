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
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCout) {
                list.add(Record.create(rs));
                return true;
            }
        };
        ing.doLoop(rs, sql.getContext());
        return ing.getList();
    }

}
