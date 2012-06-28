package org.nutz.dao.sql;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nutz.dao.FieldMatcher;
import org.nutz.dao.pager.Pager;

public class SqlContext {

    private FieldMatcher fieldMatcher;

    private Object result;

    private int updateCount;

    private int fetchSize;

    private int resultSetType;

    private Pager pager;

    private Map<String, Object> attrs;

    public SqlContext() {
        // zozoh: 默认的，SQL 的游标类型是 TYPE_FORWARD_ONLY，即，使用各个数据库自有的分页语句
        // 但是如果数据库比较原始，你可以将游标类型设置成 TYPE_SCROLL_INSENSITIVE
        // 如果你还设置了 Pager，那么执行器应该使用 JDBC 游标的方式来进行分页
        resultSetType = ResultSet.TYPE_FORWARD_ONLY;
    }

    public SqlContext attr(String name, Object value) {
        if (null == attrs) {
            attrs = new HashMap<String, Object>();
        }
        attrs.put(name, value);
        return this;
    }

    public Object attr(String name) {
        return null == attrs ? null : attrs.get(name);
    }

    public <T> T attr(Class<T> type) {
        return attr(type, type.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T attr(Class<T> classOfT, String name) {
        Object obj = attr(name);
        if (null == obj)
            return null;
        return (T) obj;
    }

    public boolean hasAttr(String name) {
        return null == attrs ? false : attrs.containsKey(name);
    }

    public Set<String> attrNames() {
        return null == attrs ? new HashSet<String>() : attrs.keySet();
    }

    public FieldMatcher getFieldMatcher() {
        return fieldMatcher;
    }

    public SqlContext setFieldMatcher(FieldMatcher matcher) {
        this.fieldMatcher = matcher;
        return this;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public int getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public int getResultSetType() {
        return resultSetType;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }

    public void setResultSetType(int resultSetType) {
        this.resultSetType = resultSetType;
    }

    public Pager getPager() {
        return pager;
    }

    public void setPager(Pager pager) {
        this.pager = pager;
        // TODO 为何要这样写??为什么?!! SQLite死活不给我全部数据!! by wendal
        // if (null != pager && pager.getPageSize() > 0)
        // this.fetchSize = pager.getPageSize();
    }

}
