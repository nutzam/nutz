package org.nutz.dao.util.cri;

import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.AbstractPItem;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;

public class SimpleCriteria extends AbstractPItem implements Criteria, OrderBy {

    private SqlExpressionGroup where;

    private OrderBySet orderBy;

    private Pager pager;

    public SimpleCriteria() {
        where = new SqlExpressionGroup();
        orderBy = new OrderBySet();
    }

    public void joinSql(Entity<?> en, StringBuilder sb) {
        where.joinSql(en, sb);
        orderBy.joinSql(en, sb);
    }

    @Override
    public void setPojo(Pojo pojo) {
        where.setPojo(pojo);
        orderBy.setPojo(pojo);
    }

    public void setPager(int pageNumber, int pageSize) {
        pager = new Pager();
        pager.setPageNumber(pageNumber);
        pager.setPageSize(pageSize);
    }

    public void setPager(Pager pager) {
        this.pager = pager;
    }

    public Pager getPager() {
        return pager;
    }

    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        return where.joinAdaptor(en, adaptors, off);
    }

    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        return where.joinParams(en, obj, params, off);
    }

    public int paramCount(Entity<?> en) {
        return where.paramCount(en);
    }

    public String toSql(Entity<?> en) {
        Object[] params = new Object[this.paramCount(en)];
        int i = where.joinParams(en, null, params, 0);
        orderBy.joinParams(en, null, params, i);

        StringBuilder sb = new StringBuilder();
        this.joinSql(en, sb);
        String[] ss = sb.toString().split("[?]");

        sb = new StringBuilder();
        for (i = 0; i < params.length; i++) {
            sb.append(ss[i]);
            sb.append(Sqls.formatFieldValue(params[i]));
        }
        if (i < ss.length)
            sb.append(ss[i]);

        return sb.toString();
    }

    public OrderBy asc(String name) {
        return orderBy.asc(name);
    }

    public OrderBy desc(String name) {
        return orderBy.desc(name);
    }

    public SqlExpressionGroup where() {
        return where;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public String toString() {
        return toSql(null);
    }
}
