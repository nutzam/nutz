package org.nutz.dao.util.cri;

import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.sql.pojo.AbstractPItem;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.GroupBy;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Pojo;

public class SimpleCriteria extends AbstractPItem implements Criteria, OrderBy, GroupBy {

    private static final long serialVersionUID = 1L;

    private SqlExpressionGroup where;

    private OrderBySet orderBy;
    
    private GroupBySet groupBy;

    private Pager pager;
    
    private String beforeWhere;

    public SimpleCriteria() {
        where = new SqlExpressionGroup();
        orderBy = new OrderBySet();
        groupBy = new GroupBySet();
    }
    
    public SimpleCriteria(String beforeWhere) {
        this();
        this.beforeWhere = beforeWhere;
    }

    @Override
    public void joinSql(Entity<?> en, StringBuilder sb) {
        if (beforeWhere != null) {
            sb.append(beforeWhere);
        }
        where.joinSql(en, sb);
        groupBy.joinSql(en, sb);
        orderBy.joinSql(en, sb);
    }

    @Override
    public void setPojo(Pojo pojo) {
        where.setPojo(pojo);
        groupBy.setPojo(pojo);
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

    @Override
    public Pager getPager() {
        return pager;
    }

    @Override
    public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
        return where.joinAdaptor(en, adaptors, off);
    }

    @Override
    public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
        return where.joinParams(en, obj, params, off);
    }

    @Override
    public int paramCount(Entity<?> en) {
        return where.paramCount(en);
    }

    @Override
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
        if (i < ss.length) {
            sb.append(ss[i]);
        }

        return sb.toString();
    }

    @Override
    public OrderBy asc(String name) {
        return orderBy.asc(name);
    }

    @Override
    public OrderBy desc(String name) {
        return orderBy.desc(name);
    }

    @Override
    public SqlExpressionGroup where() {
        return where;
    }
    
    @Override
    public GroupBy groupBy(String...names) {
    	groupBy = new GroupBySet(names);
    	return this;
    }
    
    @Override
    public GroupBy having(Condition cnd) {
    	groupBy.having(cnd);
    	return this;
    }

    @Override
    public OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public String toString() {
        return toSql(null);
    }
    
    @Override
    public OrderBy orderBy(String name, String dir) {
        if ("asc".equalsIgnoreCase(dir)) {
            this.asc(name);
        } else {
            this.desc(name);
        }
        return this;
    }
    
    @Override
    public GroupBy getGroupBy() {
        return groupBy;
    }
    
    public String getBeforeWhere() {
        return beforeWhere;
    }
}
