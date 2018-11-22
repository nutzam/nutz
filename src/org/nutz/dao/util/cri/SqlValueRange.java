package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.lang.Strings;

public class SqlValueRange extends AbstractSqlExpression {

    private static final long serialVersionUID = 6899168558668898529L;
    
    protected String sql;
    protected Object[] values;
    protected int size;
    
    protected SqlValueRange(String name, String sql, Object... values) {
        super(name);
        this.sql = sql;
        this.values = values;
        this.size = values.length;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (size == 0)
            return;
        if (not)
            sb.append(" NOT ");
        sb.append(_fmtcol(en));
        String tmp = Strings.dup("?,", size);
        sb.append(" IN (");
        sb.append(String.format(sql, tmp.substring(0, tmp.length() - 1)));
        sb.append(")");
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        if (size == 0)
            return off;
        MappingField mf = _field(en);
        ValueAdaptor adaptor = null;
        if (mf == null) {
            for (Object object : values) {
                if (object != null) {
                    adaptor = Jdbcs.getAdaptorBy(object);
                    break;
                }
            }
            if (adaptor == null)
                adaptor = Jdbcs.Adaptor.asNull;
        } else {
            adaptor = mf.getAdaptor();
        }
        for (int i = off; i < off+size; i++) {
            adaptors[i] = adaptor;
        }
        return 0;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        for (int i = off; i < off+size; i++) {
            params[i] = values[i];
        }
        return off+size;
    }

    public int paramCount(Entity<?> en) {
        return size;
    }

}
