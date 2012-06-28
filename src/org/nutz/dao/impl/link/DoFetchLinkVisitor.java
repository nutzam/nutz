package org.nutz.dao.impl.link;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nutz.dao.entity.LinkField;
import org.nutz.dao.impl.AbstractLinkVisitor;
import org.nutz.dao.sql.Pojo;
import org.nutz.dao.sql.PojoCallback;
import org.nutz.dao.util.Pojos;

public class DoFetchLinkVisitor extends AbstractLinkVisitor {

    public void visit(final Object obj, final LinkField lnk) {
        Pojo pojo = opt.maker().makeQuery(lnk.getLinkedEntity());
        pojo.setOperatingObject(obj);
        pojo.append(Pojos.Items.cnd(lnk.createCondition(obj)));
        pojo.setAfter(new PojoCallback() {
            public Object invoke(Connection conn, ResultSet rs, Pojo pojo) throws SQLException {
                Object value = lnk.getCallback().invoke(conn, rs, pojo);
                lnk.setValue(obj, value);
                return value;
            }
        });
        opt.add(pojo);
    }

}
