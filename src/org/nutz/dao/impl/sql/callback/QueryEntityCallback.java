package org.nutz.dao.impl.sql.callback;

import java.sql.ResultSet;

import java.sql.SQLException;

import org.nutz.dao.entity.Entity;

import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.SqlContext;

public class QueryEntityCallback extends EntityCallback {

    @Override
    protected Object process(final ResultSet rs, final Entity<?> entity, final SqlContext context)
            throws SQLException {
        ResultSetLooping ing = new ResultSetLooping() {
            protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
                list.add(entity.getObject(rs, context.getFieldMatcher()));
                return true;
            }
        };
        ing.doLoop(rs, context);
        return ing.getList();
    }

}
