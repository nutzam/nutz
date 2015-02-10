package org.nutz.dao.impl.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.nutz.dao.Condition;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.sql.VarIndex;
import org.nutz.dao.sql.VarSet;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;

public class NutSql extends NutStatement implements Sql {

    SqlLiteral literal;

    private VarSet vars;

    private List<VarSet> rows;

    private VarSet lastRow;

    private SqlCallback callback;

    private ValueAdaptor[] adaptors;

    private ValueAdaptor[] clientAdaptors;

    private NutSql() {
        super();
        vars = new SimpleVarSet();
        rows = new LinkedList<VarSet>();
        this.addBatch();
    }

    public NutSql(String sql) {
        this();
        this.literal = new SqlLiteral().valueOf(sql);
        this.setSqlType(this.literal.getType());
        // 根据字面量，构造一个参数的适配器数组
        adaptors = new ValueAdaptor[literal.getParamIndexes().getOrders().size()];
        clientAdaptors = new ValueAdaptor[adaptors.length];
    }

    private NutSql(SqlLiteral literal, SqlCallback callback) {
        this();
        this.literal = literal;
        this.setSqlType(this.literal.getType());
        this.callback = callback;
        // 根据字面量，构造一个参数的适配器数组
        adaptors = new ValueAdaptor[literal.getParamIndexes().getOrders().size()];
        clientAdaptors = new ValueAdaptor[adaptors.length];
    }

    public Sql setEntity(Entity<?> en) {
        return (Sql) super.setEntity(en);
    }

    public ValueAdaptor[] getAdaptors() {
        for (int i = 0; i < adaptors.length; i++) {
            // 采用用户的设定
            if (null != clientAdaptors[i])
                adaptors[i] = clientAdaptors[i];
            // 自动决定
            else if (null == adaptors[i]) {
                // 获得对应参数的名称，以及关联的其他索引
                String name = literal.getParamIndexes().getOrderName(i);
                int[] is = literal.getParamIndexes().getOrderIndex(name);
                // 寻找第一个非 null 的参数
                Object value = null;
                for (VarSet row : rows) {
                    value = row.get(name);
                    if (null != value)
                        break;
                }
                // 找到了，得到适配器，然后循环设置一下
                if (null != value) {
                    ValueAdaptor vab = Jdbcs.getAdaptor(Mirror.me(value));
                    for (int x : is)
                        adaptors[x] = vab;
                }
                // 如果找不到用 Null 适配器
                else {
                    adaptors[i] = Jdbcs.Adaptor.asNull;
                }

            }
        }
        return adaptors;
    }

    public void setValueAdaptor(String name, ValueAdaptor adaptor) {
        int[] is = literal.getParamIndexes().getOrderIndex(name);
        if (null != is)
            for (int i : is)
                adaptors[i] = adaptor;
    }

    public Object[][] getParamMatrix() {
        // 仅仅去掉队尾没有参数设定的VarSet，尽可能的不遍历
        if (rows.size() > 0) {
            VarSet vs = rows.get(rows.size() - 1);
            while (null != vs) {
                if (vs.keys().size() == 0) {
                    rows.remove(vs);
                    vs = null;
                    if (rows.size() > 0)
                        vs = rows.get(rows.size() - 1);
                } else {
                    break;
                }
            }
        }
        Object[][] re = new Object[rows.size()][adaptors.length];
        int i = 0;
        for (VarSet row : rows) {
            Object[] cols = re[i++];
            for (String name : literal.getParamIndexes().names()) {
                Object value = row.get(name);
                int[] is = literal.getParamIndexes().getOrderIndex(name);
                for (int x : is)
                    cols[x] = value;
            }
        }
        return re;
    }

    public String toPreparedStatement() {
        String[] ss = _createSqlElements();

        // 填充参数
        VarIndex vIndex = literal.getParamIndexes();
        for (String name : vIndex.names()) {
            int[] is = vIndex.indexesOf(name);
            for (int i : is)
                ss[i] = "?";

        }
        return Lang.concat(ss).toString();
    }

    /**
     * 获取语句模板并填充占位符
     * 
     * @return 语句模板
     */
    private String[] _createSqlElements() {
        String[] ss = literal.stack.cloneChain();
        VarIndex vIndex = literal.getVarIndexes();
        VarSet vs = vars;
        for (String name : vIndex.names()) {
            int[] is = vIndex.indexesOf(name);
            Object obj = vs.get(name);
            for (int i : is)
                ss[i] = null == obj ? "" : obj.toString();
        }
        return ss;
    }

    public VarSet vars() {
        return vars;
    }

    public VarSet params() {
        return lastRow;
    }

    public VarIndex varIndex() {
        return literal.getVarIndexes();
    }

    public VarIndex paramIndex() {
        return literal.getParamIndexes();
    }

    public void addBatch() {
        lastRow = new SimpleVarSet();
        rows.add(lastRow);
    }

    public void clearBatch() {
        rows.clear();
        addBatch();
    }

    public Sql setCallback(SqlCallback callback) {
        this.callback = callback;
        return this;
    }

    public Sql setCondition(Condition cnd) {
        this.vars().set("condition", cnd.toSql(this.getEntity()));
        return this;
    }

    public Sql duplicate() {
        return new NutSql(literal, callback);
    }

    public void onBefore(Connection conn) {}

    public void onAfter(Connection conn, ResultSet rs) throws SQLException {
        if (null != callback)
            getContext().setResult(callback.invoke(conn, rs, this));
    }

    @Override
    public String toString() {
        /*
         * // 语句模板 String[] ss = _createSqlElements();
         * 
         * // 填充参数 VarIndex vIndex = literal.getParamIndexes(); VarSet vs =
         * rows.get(0); for (String name : vIndex.names()) { int[] is =
         * vIndex.indexesOf(name); String s =
         * Sqls.formatFieldValue(vs.get(name)).toString(); for (int i : is)
         * ss[i] = s; }
         * 
         * return Lang.concat(ss).toString();
         */
        return super.toStatement(this.getParamMatrix(), this.toPreparedStatement());
    }

    public NutSql setPager(Pager pager) {
        this.getContext().setPager(pager);
        return this;
    }
    
    public void setSourceSql(String sql) {
        if (literal != null)
            literal.valueOf(sql);
        else
            literal = new SqlLiteral().valueOf(sql);
    }
    
    public String getSourceSql() {
        return this.literal.toString();
    }
}
