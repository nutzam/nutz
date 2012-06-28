package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;

public abstract class NumberRange extends AbstractSqlExpression {

    protected long[] ids;

    protected NumberRange(String name) {
        super(name);
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (ids.length > 0) {
            sb.append(_fmtcol(en));
            if (not)
                sb.append(" NOT");
            sb.append(" IN (");
            for (int i = 0; i < ids.length; i++)
                sb.append("?,");
            sb.setCharAt(sb.length() - 1, ')');
        } else
            ;//OK,无需添加.
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        for (int i = 0; i < ids.length; i++)
            adaptors[off++] = Jdbcs.Adaptor.asLong;
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        for (long id : ids)
            params[off++] = id;
        return off;
    }

    public int paramCount(Entity<?> en) {
        return ids.length;
    }

}
