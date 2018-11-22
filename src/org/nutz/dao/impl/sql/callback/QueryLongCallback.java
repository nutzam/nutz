package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.LinkedLongArray;

/**
 * 这个回调将返回一个长整型数组
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QueryLongCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        LinkedLongArray ary = new LinkedLongArray(20);
        while (rs.next())
            ary.push(rs.getLong(1));
        return ary.toArray();
    }

}
