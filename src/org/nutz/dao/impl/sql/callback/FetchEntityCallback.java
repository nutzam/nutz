package org.nutz.dao.impl.sql.callback;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.SqlContext;

public class FetchEntityCallback extends EntityCallback {
    
    protected String prefix;
    public FetchEntityCallback() {}
    public FetchEntityCallback(String prefix) {
        this.prefix = prefix;
    }

    protected Object process(ResultSet rs, Entity<?> entity, SqlContext context)
            throws SQLException {
        if (null != rs && rs.next())
            return entity.getObject(rs, context.getFieldMatcher(), prefix);
        return null;
    }

}
