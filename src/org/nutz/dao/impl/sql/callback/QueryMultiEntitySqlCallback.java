package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;

public class QueryMultiEntitySqlCallback extends FetchMultiEntitySqlCallback {

    public QueryMultiEntitySqlCallback() {
        super();
    }

    public QueryMultiEntitySqlCallback(Entity<?>... entites) {
        super(entites);
    }

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        int size = mappings.size();
        String[] tableNames = mappings.keySet().toArray(new String[size]);
        Entity<?>[] entites = mappings.values().toArray(new Entity<?>[size]);
        List<Object[]> list = new LinkedList<Object[]>();
        while (rs.next()) {
            list.add(next(mappings, rs, tableNames, entites));
        }
        return list;
    }
}
