package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;

/**
 * 返回 String 列表
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QueryStringCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        List<String> list = new LinkedList<String>();
        while (rs.next())
            list.add(rs.getString(1));
        return list;
    }

}
