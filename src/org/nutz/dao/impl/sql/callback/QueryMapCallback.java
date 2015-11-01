package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedHashMap;

import org.nutz.dao.DaoException;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.Streams;

public class QueryMapCallback implements SqlCallback {

    public final static SqlCallback me = new QueryMapCallback();

    public Object invoke(Connection conn, ResultSet rs, Sql sql)
            throws SQLException {

        final ResultSetMetaData meta = rs.getMetaData();
        final int count = meta.getColumnCount();
        // ResultSetLooping 封装了遍历结果集的方法,里面包含了针对sqlserver等浮标型分页的支持
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index,
                                           ResultSet rs,
                                           SqlContext context,
                                           int rowCout) {
                String name = null;
                int i = 0;
                try {
                    LinkedHashMap<String, Object> re = new LinkedHashMap<String, Object>();
                    for (i = 1; i <= count; i++) {
                        name = meta.getColumnLabel(i);
                        switch (meta.getColumnType(i)) {
                        case Types.TIMESTAMP: {
                            re.put(name, rs.getTimestamp(i));
                            break;
                        }
                        case Types.DATE: {// ORACLE的DATE类型包含时间,如果用默认的只有日期没有时间
                                          // from
                                          // cqyunqin
                            re.put(name, rs.getTimestamp(i));
                            break;
                        }
                        case Types.CLOB: {
                            re.put(name, Streams.read(rs.getCharacterStream(i))
                                                .toString());
                            break;
                        }
                        default:
                            re.put(name, rs.getObject(i));
                            break;
                        }
                    }
                    list.add(re);
                    return true;
                }
                catch (Exception e) {
                    if (name != null) {
                        throw new DaoException(String.format("Column Name=%s, index=%d",
                                                             name,
                                                             i),
                                               e);
                    }
                    throw new DaoException(e);
                }
            }
        };
        ing.doLoop(rs, sql.getContext());
        return ing.getList();
    }

}
