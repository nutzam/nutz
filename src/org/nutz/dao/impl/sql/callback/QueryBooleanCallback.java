package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.util.LinkedArray;

/**
 * 这个回调将返回一个 boolean 值
 * 
 * @author Conanca(conanca2006@gmail.com)
 */
public class QueryBooleanCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        LinkedArray<Boolean> array = new LinkedArray<Boolean>();
        if (null != rs && rs.next())
            array.push(rs.getBoolean(1));
        return array;
    }

}
