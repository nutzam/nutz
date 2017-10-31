package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

/**
 * 这个回调将返回一个 boolean 值
 * 
 * @author Conanca(conanca2006@gmail.com)
 */
public class QueryBooleanCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        List<Boolean> list = new LinkedList<Boolean>();
        if (null != rs && rs.next())
            list.add(rs.getBoolean(1));
        boolean[] array = new boolean[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

}
