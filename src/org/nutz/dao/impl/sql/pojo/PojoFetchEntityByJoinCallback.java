package org.nutz.dao.impl.sql.pojo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nutz.dao.FieldFilter;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.LinkVisitor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;

public class PojoFetchEntityByJoinCallback implements PojoCallback {
    
    protected String regex;
    
    public PojoFetchEntityByJoinCallback(String regex) {
        this.regex = regex;
    }

    public Object invoke(Connection conn, final ResultSet rs, Pojo pojo, Statement stmt)
            throws SQLException {
        if (null != rs && rs.next()) {
            final Object mainObject = pojo.getEntity().getObject(rs, pojo.getContext().getFieldMatcher(), null);
            pojo.getEntity().visitOne(mainObject, regex, new LinkVisitor() {
                public void visit(Object obj, LinkField lnk) {
                    Entity<?> en  = lnk.getLinkedEntity();
                    String prefix = en.getTableName() + "_z_";
                    Object linkObject = en.getObject(rs, FieldFilter.get(en.getType()), prefix);
                    lnk.setValue(mainObject, linkObject);
                }
            });
            return mainObject;
        }
        return null;
    }

}
