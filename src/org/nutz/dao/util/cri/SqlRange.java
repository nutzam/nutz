package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.util.lambda.PFun;

public class SqlRange extends NoParamsSqlExpression implements SqlExpression {

    private static final long serialVersionUID = 1L;

    protected String sql;

    protected SqlRange(String name) {
        super(name);
    }

    protected <T> SqlRange(PFun<T, ?> name) {
        super(name);
    }

    public SqlRange(String name, String fmt, Object... args) {
        super(name);
        this.not = false;
        if (fmt != null)
            this.sql = String.format(fmt, args);
    }

    public <T> SqlRange(PFun<T, ?> name, String fmt, Object... args) {
        super(name);
        this.not = false;
        if (fmt != null)
            this.sql = String.format(fmt, args);
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(String.format("%s%s IN (%s)", (not ? " NOT " : ""), _fmtcol(en), sql));
    }

}
