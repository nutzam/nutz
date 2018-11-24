package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

public class FetchRecordCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        if (null != rs && rs.next()) {
            return Record.create(rs);
        }
        return null;
    }

}
