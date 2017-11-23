package org.nutz.dao.impl.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.jdbc.ValueAdaptor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.sql.SqlType;
import org.nutz.dao.util.Daos;
import org.nutz.dao.util.blob.SimpleBlob;
import org.nutz.dao.util.blob.SimpleClob;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public abstract class NutStatement implements DaoStatement {

    private static final long serialVersionUID = 1L;

    private Entity<?> entity;

    private SqlContext context;

    private SqlType sqlType;
    
    protected JdbcExpert expert;
    
    public NutStatement() {
        this.context = new SqlContext();
    }

    public boolean isSelect() {
        return SqlType.SELECT == sqlType;
    }

    public boolean isUpdate() {
        return SqlType.UPDATE == sqlType;
    }

    public boolean isDelete() {
        return SqlType.DELETE == sqlType;
    }

    public boolean isInsert() {
        return SqlType.INSERT == sqlType;
    }

    public boolean isCreate() {
        return SqlType.CREATE == sqlType;
    }

    public boolean isDrop() {
        return SqlType.DROP == sqlType;
    }

    public boolean isRun() {
        return SqlType.RUN == sqlType;
    }

    public boolean isAlter() {
        return SqlType.ALTER == sqlType;
    }
    
    public boolean isExec() {
    	return SqlType.EXEC == sqlType;
    }
    
    public boolean isCall() {
    	return SqlType.CALL == sqlType;
    }

    public boolean isOther() {
        return SqlType.OTHER == sqlType;
    }

    public Entity<?> getEntity() {
        return entity;
    }

    public DaoStatement setEntity(Entity<?> entity) {
        this.entity = entity;
        return this;
    }

    public SqlContext getContext() {
        return context;
    }

    public void setContext(SqlContext context) {
        this.context = context;
    }

    public SqlType getSqlType() {
        return sqlType;
    }

    public DaoStatement setSqlType(SqlType sqlType) {
        this.sqlType = sqlType;
        return this;
    }

    public Object getResult() {
        return context.getResult();
    }

    // TODO 是不是太暴力了涅~~~ --> 不是一般的暴力!!
    @SuppressWarnings("unchecked")
    public <T> List<T> getList(Class<T> classOfT) {
        Object re = getResult();
        if (re == null)
            return null;
        if (re.getClass().isArray()) {
            return Lang.array2list(re, classOfT);
        }
        return (List<T>) re;// TODO 考虑先遍历转换一次
    }

    public <T> T getObject(Class<T> classOfT) {
        return Castors.me().castTo(getResult(), classOfT);
    }

    public int getInt() {
        return getNumber().intValue();
    }
    
    public int getInt(int defaultValue) {
        Number re = getNumber();
        if (re == null)
            return defaultValue;
        return re.intValue();
    }

    public long getLong() {
        return getNumber().longValue();
    }
    public long getLong(long defaultValue) {
        Number re = getNumber();
        if (re == null)
            return defaultValue;
        return re.longValue();
    }

    public double getDouble() {
        return getNumber().doubleValue();
    }

    public double getDouble(double defaultValue) {
        Number re = getNumber();
        if (re == null)
            return defaultValue;
        return re.doubleValue();
    }

    public float getFloat() {
        return getNumber().floatValue();
    }

    public float getFloat(float defaultValue) {
        Number re = getNumber();
        if (re == null)
            return defaultValue;
        return re.floatValue();
    }

    public Number getNumber() {
    	return getObject(Number.class);
    }

    public String getString() {
        return getObject(String.class);
    }

    public boolean getBoolean() {
        return getObject(Boolean.class);
    }

    public int getUpdateCount() {
        return context.getUpdateCount();
    }

    public String forPrint() {
        String sql = this.toPreparedStatement();
        StringBuilder sb = new StringBuilder(sql);
        // 准备打印参数表
        Object[][] mtrx = this.getParamMatrix();
        SqlFormat format = Daos.getSqlFormat();
        if (null != mtrx && mtrx.length > 0 && mtrx[0].length > 0) {
            if (format.isPrintParam()) {
                // 计算每列最大宽度，以及获取列参数的内容
                int[] maxes = new int[mtrx[0].length];
                String[][] sss = new String[mtrx.length][mtrx[0].length];
                for (int row = 0; row < mtrx.length; row++)
                    for (int col = 0; col < mtrx[0].length; col++) {
                        String s = param2String(mtrx[row][col]);
                        maxes[col] = Math.max(maxes[col], s.length());
                        if (format.getParamLengthLimit() > 0 && maxes[col] > format.getParamLengthLimit())
                            maxes[col] = format.getParamLengthLimit();
                        sss[row][col] = s;
                    }
                // 输出表头
                sb.append("\n    |");
                for (int i = 0; i < mtrx[0].length; i++) {
                    sb.append(' ');
                    sb.append(Strings.alignRight("" + (i + 1), maxes[i], ' '));
                    sb.append(" |");
                }
                // 输出分隔线
                sb.append("\n    |");
                for (int i = 0; i < mtrx[0].length; i++) {
                    sb.append('-');
                    sb.append(Strings.dup('-', maxes[i]));
                    sb.append("-|");
                }

                // 输出内容到字符串缓冲区
                int maxRow = mtrx.length > format.getParamRowLimit() ? format.getParamRowLimit() : mtrx.length;
                for (int row = 0; row < maxRow; row++) {
                    sb.append("\n    |");
                    for (int col = 0; col < mtrx[0].length; col++) {
                        sb.append(' ');
                        sb.append(sss[row][col].length() > maxes[col] ? Strings.brief(sss[row][col], maxes[col] - 5) : Strings.alignLeft(sss[row][col], maxes[col], ' '));
                        sb.append(" |");
                    }
                }

                if (maxRow != mtrx.length)
                    sb.append("\n -- Only display first " + maxRow + " lines , don't show the remaining record(count=" + mtrx.length + ")");
            }
            if (format.isPrintExample()) {
                // 输出可执行的 SQL 语句, TODO 格式非常不好看!!如果要复制SQL,很麻烦!!!
                sb.append("\n  For example:> \"");
                sb.append(toExampleStatement(mtrx, sql));
                sb.append('"');
            }
        }
        return sb.toString();
    }

    protected String toExampleStatement(Object[][] mtrx, String sql) {
        return toStatement(mtrx, sql);
    }

    protected String toStatement(Object[][] mtrx, String sql) {
        StringBuilder sb = new StringBuilder();
        String[] ss = sql.split("[?]");
        int i = 0;
        if (mtrx.length > 0) {
            for (; i < mtrx[0].length; i++) {
                sb.append(ss[i]);
                Object tmp = mtrx[0][i];
                if (tmp == null) {
                    sb.append("NULL");
                }
                else if (tmp instanceof Number || tmp instanceof Boolean) {
                    sb.append(tmp.toString());
                } else {
                    sb.append(Sqls.formatFieldValue(param2String(tmp)));
                }
            }
        }
        for (; i < ss.length; i++)
        	sb.append(ss[i]);
        return sb.toString();
    }

    protected String param2String(Object obj) {
        if (obj == null)
            return "NULL";
        if (obj instanceof CharSequence)
            return obj.toString();
        else {
            if (obj instanceof Blob) {
                Blob blob = (Blob) obj;
                if (blob instanceof SimpleBlob) {
                    try {
                        return "*Blob(len=" + blob.length() + ")";
                    }
                    catch (SQLException e) {}// 不可能
                }
                return "*Blob(hascode=" + blob.hashCode() + ")";
            } else if (obj instanceof Clob) {
                Clob clob = (Clob) obj;
                if (clob instanceof SimpleClob) {
                    try {
                        return "*Clob(len=" + clob.length() + ")";
                    }
                    catch (SQLException e) {}// 不可能
                }
                return "*Clob(" + clob.hashCode() + ")";
            } else if (obj instanceof byte[] || obj instanceof char[]) {
                return "*"+(obj instanceof byte[] ? "byte" : "char" ) + "[len=" + Array.getLength(obj) + "]";
            } else if (obj instanceof InputStream) {
                try {
                    obj = "*InputStream[len=" + ((InputStream) obj).available() + "]";
                }
                catch (IOException e) {}
            } else if (obj instanceof Reader) {
                obj = "*Reader@" + obj.hashCode();
            }
            return Castors.me().castToString(obj);
        }
    }

    public void forceExecQuery() {
    	this.sqlType = SqlType.SELECT;
    }
    
    public boolean isForceExecQuery() {
    	return isSelect();
    }

    public String toString() {
        return toStatement(this.getParamMatrix(), this.toPreparedStatement());
    }
    
    public void setExpert(JdbcExpert expert) {
        this.expert = expert;
    }
    
    protected ValueAdaptor getAdapterBy(Object value) {
        if (value == null)
            return Jdbcs.Adaptor.asNull;
        if (expert == null)
            return Jdbcs.getAdaptorBy(value);
        NutMappingField mf = new NutMappingField(entity);
        mf.setType(value.getClass());
        Jdbcs.guessEntityFieldColumnType(mf);
        return expert.getAdaptor(mf);
    }
}
