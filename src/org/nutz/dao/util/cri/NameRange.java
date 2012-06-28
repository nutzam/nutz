package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;

public class NameRange extends AbstractSqlExpression {

    private String[] names;

    NameRange(String name, String... names) {
        super(name);
        this.names = names;
        this.not = false;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (names.length > 0) {
            sb.append(_fmtcol(en));
            if (not)
                sb.append(" NOT");
            sb.append(" IN (");
            for (int i = 0; i < names.length; i++)
                sb.append("?,");
            sb.setCharAt(sb.length() - 1, ')');
        } else
            ;//OK,无需添加.
    }

    public int joinAdaptor(Entity<?> en,ValueAdaptor[] adaptors, int off) {
        for (int i = 0; i < names.length; i++)
            adaptors[off++] = Jdbcs.Adaptor.asString;
        return off;
    }

    public int joinParams(Entity<?> en,Object obj, Object[] params, int off) {
        for (String name : names)
            params[off++] = name;
        return off;
    }

    public int paramCount(Entity<?> en) {
        return names.length;
    }

}
