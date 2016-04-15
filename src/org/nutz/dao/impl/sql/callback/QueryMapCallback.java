package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlContext;

public class QueryMapCallback implements SqlCallback {

    public final static SqlCallback me = new QueryMapCallback();

    public Object invoke(Connection conn, ResultSet rs, Sql sql)
            throws SQLException {
        final ResultSetMetaData meta = rs.getMetaData();
        // ResultSetLooping 封装了遍历结果集的方法,里面包含了针对sqlserver等浮标型分页的支持
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index,
                                           ResultSet rs,
                                           SqlContext context,
                                           int rowCout) {
                LinkedHashMap<String, Object> re = new LinkedHashMap<String, Object>();
                Record.create(re, rs, meta);
                list.add(re);
                return true;
            }
        };
        ing.doLoop(rs, sql.getContext());
        return ing.getList();
    }

}
