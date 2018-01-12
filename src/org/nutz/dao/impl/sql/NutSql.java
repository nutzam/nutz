package org.nutz.dao.impl.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.Record;
import org.nutz.dao.impl.sql.pojo.AbstractPItem;
import org.nutz.dao.impl.sql.pojo.StaticPItem;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.PItem;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.VarIndex;
import org.nutz.dao.sql.VarSet;
import org.nutz.dao.util.Pojos;
import org.nutz.lang.Each;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class NutSql extends NutStatement implements Sql {

    private static final long serialVersionUID = 1L;

    protected String sourceSql;
    protected VarSet vars;
    protected List<VarSet> rows;
    protected VarSet params;
    protected SqlCallback callback;
    protected VarIndex varIndex;
    protected VarIndex paramIndex;
    protected Map<String, ValueAdaptor> customValueAdaptor;
    protected List<PItem> items;
    protected char[] placeholder;

    public NutSql(String source) {
        this(source, null);
    }

    public NutSql(String source, SqlCallback callback) {
        this.setSourceSql(source);
        this.callback = callback;
        this.vars = new SimpleVarSet();
        this.rows = new ArrayList<VarSet>();
        this.params = new SimpleVarSet();
        this.rows.add(params);
        customValueAdaptor = new HashMap<String, ValueAdaptor>();
    }

    public void setSourceSql(String sql) {
        this.sourceSql = sql;
        SqlLiteral literal = literal();
        this.varIndex = literal.getVarIndexes();
        this.paramIndex = literal.getParamIndexes();
        if (getSqlType() == null)
            setSqlType(literal.getType());
        String[] ss = literal.stack.cloneChain();
        PItem[] tmp = new PItem[ss.length];
        for (String var : varIndex.getOrders()) {
            int[] is = varIndex.indexesOf(var);
            if (is != null) {
                for (int i : is) {
                    tmp[i] = new SqlVarPItem(var);
                }
            }
        }
        for (String param : paramIndex.getOrders()) {
            int[] is = paramIndex.indexesOf(param);
            if (is != null) {
                for (int i : is) {
                    tmp[i] = new SqlParamPItem(param);
                }
            }
        }
        for (int i = 0; i < tmp.length; i++) {
            if (tmp[i] == null) {
                tmp[i] = new StaticPItem(ss[i], true);
            }
        }
        this.items = Arrays.asList(tmp);
    }

    protected int _params_count() {
        int count = 0;
        Entity<?> en = getEntity();
        for (PItem item : items) {
            count += item.paramCount(en);
        }
        return count;
    }

    public ValueAdaptor[] getAdaptors() {
        ValueAdaptor[] adaptors = new ValueAdaptor[_params_count()];
        int i = 0;
        for (PItem item : items)
            i = item.joinAdaptor(getEntity(), adaptors, i);
        return adaptors;
    }

    public Object[][] getParamMatrix() {
        int pc = _params_count();
        int row_count = rows.size();
        if (rows.size() > 1 && params.size() == 0 && rows.get(0).size() != 0) {
            row_count--;
        }
        Object[][] re = new Object[row_count][pc];
        for (int z = 0; z < row_count; z++) {
            VarSet row = rows.get(z);
            int i = 0;
            for (PItem item : items)
                i = item.joinParams(getEntity(), row, re[z], i);
        }
        return re;
    }

    public String toPreparedStatement() {
        StringBuilder sb = new StringBuilder();
        for (PItem item : items)
            item.joinSql(getEntity(), sb);
        return sb.toString();
    }

    public void onBefore(Connection conn) throws SQLException {}

    public void onAfter(Connection conn, ResultSet rs, Statement stmt) throws SQLException {
        if (callback != null)
            getContext().setResult(callback.invoke(conn, rs, this));
    }

    public DaoStatement setPager(Pager pager) {
        getContext().setPager(pager);
        return this;
    }

    public VarSet vars() {
        return vars;
    }

    public VarSet params() {
        return params;
    }

    public void setValueAdaptor(String name, ValueAdaptor adaptor) {
        this.customValueAdaptor.put(name, adaptor);
    }

    public VarIndex varIndex() {
        return varIndex;
    }

    public VarIndex paramIndex() {
        return paramIndex;
    }

    public void addBatch() {
        params = new SimpleVarSet();
        rows.add(params);
    }

    public void clearBatch() {
        params = new SimpleVarSet();
        rows.clear();
        rows.add(params);
    }

    public Sql setEntity(Entity<?> entity) {
        super.setEntity(entity);
        return this;
    }

    public Sql setCallback(SqlCallback callback) {
        this.callback = callback;
        return this;
    }

    public Sql setCondition(Condition cnd) {
        vars.set("condition", cnd);
        return this;
    }

    public Sql duplicate() {
        return new NutSql(sourceSql, callback);
    }

    public String getSourceSql() {
        return sourceSql;
    }

    class SqlVarPItem extends AbstractPItem {

        /**
		 * 
		 */
		private static final long serialVersionUID = 2655530650031939556L;
		public String name;

        public SqlVarPItem(String name) {
            this.name = name;
        }

        public void joinSql(Entity<?> en, StringBuilder sb) {
            Object val = vars.get(name);
            if (val != null) {
                if (val instanceof PItem) {
                    ((PItem) val).joinSql(en, sb);
                }
                else if (val instanceof Condition) {
                    sb.append(' ').append(Pojos.formatCondition(en, (Condition) val));
                } else {
                    sb.append(val);
                }
            }
        }
        
        public int joinAdaptor(Entity<?> en, ValueAdaptor[] adaptors, int off) {
            Object val = vars.get(name);
            if (val != null) {
                if (val instanceof PItem) {
                    return ((PItem) val).joinAdaptor(en, adaptors, off);
                }
            }
            return off;
        }
        
        public int paramCount(Entity<?> en) {
            Object val = vars.get(name);
            if (val != null) {
                if (val instanceof PItem) {
                    return ((PItem) val).paramCount(en);
                }
            }
            return 0;
        }
        
        public int joinParams(Entity<?> en, Object obj, Object[] params, int off) {
            Object val = vars.get(name);
            if (val != null) {
                if (val instanceof PItem) {
                    return ((PItem) val).joinParams(en, obj, params, off);
                }
            }
            return off;
        }
    }

    class SqlParamPItem extends AbstractPItem {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1494513192752663060L;
		public String name;

        public SqlParamPItem(String name) {
            this.name = name;
        }

        public void joinSql(Entity<?> en, StringBuilder sb) {
            Object val = rows.get(0).get(name);
            if (val == null) {
                sb.append("?");
            } else if (val instanceof PItem) {
                ((PItem) val).joinSql(en, sb);
            } else if (val.getClass().isArray()) {
                sb.append(Strings.dup("?,", Lang.eleSize(val)));
                sb.setLength(sb.length() - 1);
            } else if (val instanceof Condition) {
                sb.append(' ').append(Pojos.formatCondition(en, (Condition) val));
            } else {
                sb.append("?");
            }
        }

        public int joinAdaptor(final Entity<?> en, final ValueAdaptor[] adaptors, final int off) {
            if (!customValueAdaptor.isEmpty()) {
                ValueAdaptor custom = customValueAdaptor.get(name);
                if (custom != null) {
                    adaptors[off] = custom;
                    return off + 1;
                }
            }
            Object val = rows.get(0).get(name);
            if (val == null && rows.size() > 1) {
                for (VarSet vs : rows) {
                    val = vs.get(name);
                    if (val != null)
                        break;
                }
            }
            if (val == null) {
                adaptors[off] = getAdapterBy(null);
                return off + 1;
            } else if (val instanceof PItem) {
                return ((PItem) val).joinAdaptor(en, adaptors, off);
            } else if (val.getClass().isArray() || Collection.class.isAssignableFrom(val.getClass())) {
                int len = Lang.eleSize(val);
                Lang.each(val, new Each<Object>() {
                    public void invoke(int index, Object ele, int length) {
                        adaptors[off + index] = getAdapterBy(ele);
                    }
                });
                return off + len;
                // } else if (val instanceof Condition) {

            } else {
                adaptors[off] = getAdapterBy(val);
                return off + 1;
            }
        }

        public int joinParams(Entity<?> en, Object obj, final Object[] params, final int off) {
            VarSet row = (VarSet) obj;
            Object val = row.get(name);
            if (val == null) {
                return off + 1;
            } else if (val instanceof PItem) {
                return ((PItem) val).joinParams(en, null, params, off);
            } else if (val.getClass().isArray()) {
                int len = Lang.eleSize(val);
                Lang.each(val, new Each<Object>() {
                    public void invoke(int index, Object ele, int length) {
                        params[off + index] = ele;
                    }
                });
                return off + len;
                // } else if (val instanceof Condition) {

            } else {
                params[off] = val;
                return off + 1;
            }
        }

        public int paramCount(Entity<?> en) {
            Object val = rows.get(0).get(name);
            if (val == null) {
                return 1;
            } else if (val instanceof PItem) {
                return ((PItem) val).paramCount(en);
            } else if (val.getClass().isArray()) {
                return Lang.eleSize(val);
            } else if (val instanceof Condition) {
                return 0;
            } else {
                return 1;
            }
        }
    }
    
    /**
     * 若需要定制参数字符和变量字符,覆盖本方法,通过SqlLiteral的构造方法指定之
     */
    protected SqlLiteral literal() {
        if (placeholder == null)
            return new SqlLiteral().valueOf(sourceSql);
        return new SqlLiteral(placeholder[0], placeholder[1]).valueOf(sourceSql);
    }
    
    public Sql setParam(String name, Object value) {
        params().set(name, value);
        return this;
    }
    
    public Sql setVar(String name, Object value) {
        vars().set(name, value);
        return this;
    }
    
    public Record getOutParams() {
        return getContext().attr(Record.class, "OUT");
    }
    
    public Sql changePlaceholder (char param, char var) {
        placeholder = new char[]{param, var};
        setSourceSql(getSourceSql());
        return null;
    }
}
