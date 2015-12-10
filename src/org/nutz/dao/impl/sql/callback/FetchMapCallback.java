package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

public class FetchMapCallback implements SqlCallback {
    
    public static SqlCallback me = new FetchMapCallback();

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        if (null != rs && rs.next()) {
            LinkedHashMap<String, Object> re = new LinkedHashMap<String, Object>();
            Record.create(re, rs, null);
            return re;
        }
        return null;
    }

}
