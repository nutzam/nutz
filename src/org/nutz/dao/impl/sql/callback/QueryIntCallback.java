package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.LinkedIntArray;

/**
 * 这个回调将返回一个 Int 数组
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QueryIntCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        LinkedIntArray ary = new LinkedIntArray(20);
        while (rs.next())
            ary.push(rs.getInt(1));
        return ary.toArray();
    }

}
