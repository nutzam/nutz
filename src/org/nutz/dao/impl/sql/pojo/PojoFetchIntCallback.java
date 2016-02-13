package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;

public class PojoFetchIntCallback implements PojoCallback {

    public Object invoke(Connection conn, ResultSet rs, Pojo pojo, Statement stmt) throws SQLException {
        if(null!=rs && rs.next()){
            return rs.getInt(1);
        }
        return -1;
    }

}
