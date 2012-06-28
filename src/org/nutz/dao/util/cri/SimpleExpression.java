package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;

public class SimpleExpression extends AbstractSqlExpression {

    private String op;
    private Object value;

    public SimpleExpression(String name, String op, Object val) {
        super(name);
        this.op = op;
        this.value = val;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (not)
            sb.append(" NOT ");
        if ("=".equals(op) || ">".equals(op) || "<".equals(op) || "!=".equals(op))
            sb.append(_fmtcol(en)).append(op).append('?');
        else
            sb.append(_fmtcol(en)).append(' ').append(op).append(' ').append('?');
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        MappingField mf = _field(en);
        if (null != mf) {
            adaptors[off++] = mf.getAdaptor();
        } else {
            adaptors[off++] = Jdbcs.getAdaptorBy(value);
        }
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        params[off++] = value;
        return off;
    }

    public int paramCount(Entity<?> en) {
        return 1;
    }

}
