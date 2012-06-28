package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;

public class PojoFetchRecordCallback implements PojoCallback {

    public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
        if (null != rs && rs.next()) {
            return Record.create(rs);
        }
        return null;
    }

}
