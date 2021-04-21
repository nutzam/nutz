package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.util.lambda.PFun;

public class IsNull extends NoParamsSqlExpression {

    private static final long serialVersionUID = 1L;

    public IsNull(String name) {
        super(name);
        this.not = false;
    }

    public <T> IsNull(PFun<T, ?> name) {
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
