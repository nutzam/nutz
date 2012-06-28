package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;

public class SqlRange extends NoParamsSqlExpression implements SqlExpression {

    private String sql;

    public SqlRange(String name, String fmt, Object... args) {
        super(name);
        this.not = false;
        this.sql = String.format(fmt, args);
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(String.format("%s%s IN (%s)", (not ? " NOT " : ""), _fmtcol(en), sql));
    }

}
