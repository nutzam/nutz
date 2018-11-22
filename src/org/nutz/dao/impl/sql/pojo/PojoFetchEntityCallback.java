package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;

public class PojoFetchEntityCallback implements PojoCallback {
    
    protected String prefix;
    public PojoFetchEntityCallback() {}
    public PojoFetchEntityCallback(String prefix) {
        this.prefix = prefix;
    }

    public Object invoke(Connection conn, ResultSet rs, Pojo pojo, Statement stmt) throws SQLException {
        if (null != rs && rs.next())
            return pojo.getEntity().getObject(rs, pojo.getContext().getFieldMatcher(), prefix);
        return null;
    }

}
