package org.nutz.dao.impl.sql.callback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.FieldMatcher;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.SqlContext;
import org.nutz.lang.Lang;

public abstract class EntityCallback implements SqlCallback {

    public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
        Entity<?> en = sql.getEntity();
        if (null == en)
            throw Lang.makeThrow("SQL without entity : %s", sql.toString());
        FieldMatcher fmh = sql.getContext().getFieldMatcher();
        if (null == fmh)
            sql.getContext().setFieldMatcher(FieldFilter.get(en.getType()));
        return process(rs, en, sql.getContext());
    }

    protected abstract Object process(ResultSet rs, Entity<?> entity, SqlContext context)
            throws SQLException;
}
