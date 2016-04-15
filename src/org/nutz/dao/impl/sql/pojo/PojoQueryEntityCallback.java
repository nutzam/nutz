package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.pager.ResultSetLooping;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.sql.SqlContext;

public class PojoQueryEntityCallback implements PojoCallback {
    
    protected String prefix;
    public PojoQueryEntityCallback() {}
    public PojoQueryEntityCallback(String prefix) {
        this.prefix = prefix;
    }

    public Object invoke(Connection conn, ResultSet rs, final Pojo pojo, Statement stmt) throws SQLException {
        ResultSetLooping ing =  new ResultSetLooping() {
            protected boolean createObject(int index, ResultSet rs, SqlContext context, int rowCount) {
                list.add(pojo.getEntity().getObject(rs, context.getFieldMatcher(), prefix));
                return true;
            }
        };
        ing.doLoop(rs, pojo.getContext());
        return ing.getList();
    }

}
