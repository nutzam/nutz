package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.ValueAdaptor;

public abstract class NoParamsSqlExpression extends AbstractSqlExpression {

    private static final long serialVersionUID = 1L;

    protected NoParamsSqlExpression(String name) {
        super(name);
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        return off;
    }

    public int paramCount(Entity<?> en) {
        return 0;
    }

}
