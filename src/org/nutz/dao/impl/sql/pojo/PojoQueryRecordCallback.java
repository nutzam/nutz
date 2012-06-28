package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlContext;

public class PojoQueryRecordCallback implements PojoCallback {

    public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
                list.add(Record.create(rs));
                return true;
            }
        };
        ing.doLoop(rs, pojo.getContext());
        return ing.getList();
    }

}
