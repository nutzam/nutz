package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;

public class IsNull extends NoParamsSqlExpression {

    public IsNull(String name) {
        super(name);
        this.not = false;
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        sb.append(_fmtcol(en));
        sb.append(" IS ");
        if (not)
            sb.append("NOT ");
        sb.append("NULL ");
    }

}
