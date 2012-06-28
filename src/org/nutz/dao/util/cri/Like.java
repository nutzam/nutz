package org.nutz.dao.util.cri;

import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;

public class Like extends AbstractSqlExpression {

    static Like create(String name, String value, boolean ignoreCase) {
        Like like = new Like(name);
        like.value = value;
        like.ignoreCase = ignoreCase;
        like.left = "%";
        like.right = "%";
        return like;
    }

    private String value;

    private boolean ignoreCase;

    private String left;

    private String right;

    private Like(String name) {
        super(name);
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        String colName = _fmtcol(en);
        if (not)
            sb.append(" NOT ");
        if (ignoreCase)
            sb.append("LOWER(").append(colName).append(") LIKE LOWER(?)");
        else
            sb.append(colName).append(" LIKE ?");

    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        adaptors[off++] = Jdbcs.Adaptor.asString;
        return off;
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        params[off++] = (null == left ? "" : left) + value + (null == right ? "" : right);
        return off;
    }

    public int paramCount(Entity<?> en) {
        return 1;
    }

    public Like left(String left) {
        this.left = left;
        return this;
    }

    public Like right(String right) {
        this.right = right;
        return this;
    }

    public Like ignoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
        return this;
    }

}
