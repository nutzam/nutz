package org.nutz.dao.impl.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.util.List;

import org.nutz.castor.Castors;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.dao.sql.SqlContext;
import org.nutz.dao.sql.SqlType;
import org.nutz.lang.Strings;

public abstract class NutStatement implements DaoStatement {

    private Entity<?> entity;

    private SqlContext context;

    private SqlType sqlType;

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
        return (List<T>) getResult();// TODO 考虑先遍历转换一次
    }

    public <T> T getObject(Class<T> classOfT) {
        return Castors.me().castTo(getResult(), classOfT);
    }

    public int getInt() {
        Integer i = getObject(Integer.class);
        if (i == null)
            return 0;// TODO 是不是应该抛出异常呢?
        return i;// TODO 怪怪的,如果getObject返回null,这里就NPE了 by zozoh
                 // 因为自动解包的原因,by wendal
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

    public String toString() {
        String sql = this.toPreparedStatement();
        StringBuilder sb = new StringBuilder(sql);

        // 准备打印参数表
        Object[][] mtrx = this.getParamMatrix();
        if (null != mtrx && mtrx.length > 0 && mtrx[0].length > 0) {
            // 计算每列最大宽度，以及获取列参数的内容
            int[] maxes = new int[mtrx[0].length];
            String[][] sss = new String[mtrx.length][mtrx[0].length];
            for (int row = 0; row < mtrx.length; row++)
                for (int col = 0; col < mtrx[0].length; col++) {
                    String s = param2String(mtrx[row][col]);
                    maxes[col] = Math.max(maxes[col], s.length());
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
            // XXX 只输出50行
            int maxRow = mtrx.length > 50 ? 50 : mtrx.length;
            for (int row = 0; row < maxRow; row++) {
                sb.append("\n    |");
                for (int col = 0; col < mtrx[0].length; col++) {
                    sb.append(' ');
                    sb.append(Strings.alignLeft(sss[row][col], maxes[col], ' '));
                    sb.append(" |");
                }
            }

            if (maxRow != mtrx.length)
                sb.append("\n  .............................................")
                  .append("\n  !!!Too many data . Only display 50 lines , don't show the remaining record")
                  .append("\n  .............................................");
            // 输出可执行的 SQL 语句, TODO 格式非常不好看!!如果要复制SQL,很麻烦!!!
            sb.append("\n  For example:> \"");
            sb.append(toExampleStatement(mtrx, sql));
            sb.append('"');
        }

        return sb.toString();
    }

    protected String toExampleStatement(Object[][] mtrx, String sql) {
        StringBuilder sb = new StringBuilder();
        String[] ss = sql.split("[?]");
        int i = 0;
        if (mtrx.length > 0) {
            for (; i < mtrx[0].length; i++) {
                sb.append(ss[i]);
                Object obj = mtrx[0][i];
                if (obj != null) {
                    if (obj instanceof Blob) {
                        Blob blob = (Blob) obj;
                        obj = "Blob(" + blob.hashCode() + ")";
                    } else if (obj instanceof Clob) {
                        Clob clob = (Clob) obj;
                        obj = "Clob(" + clob.hashCode() + ")";
                    } else if (obj instanceof byte[] || obj instanceof char[]) {
                        if (Array.getLength(obj) > 10240)
                            obj = "*BigData[len=" + Array.getLength(obj) + "]";
                    } else if (obj instanceof InputStream) {
                        try {
                            obj = "*InputStream[len=" + ((InputStream) obj).available() + "]";
                        }
                        catch (IOException e) {}
                    } else if (obj instanceof Reader) {
                        obj = "*Reader@" + obj.hashCode();
                    }
                }
                sb.append(Sqls.formatFieldValue(obj));
            }
        }
        if (i < ss.length)
            sb.append(ss[i]);

        return sb.toString();
    }

    protected String toStatement(Object[][] mtrx, String sql) {
        StringBuilder sb = new StringBuilder();
        String[] ss = sql.split("[?]");
        int i = 0;
        if (mtrx.length > 0) {
            for (; i < mtrx[0].length; i++) {
                sb.append(ss[i]);
                sb.append(Sqls.formatFieldValue(mtrx[0][i]));
            }
        }
        if (i < ss.length)
            sb.append(ss[i]);

        return sb.toString();
    }

    protected String param2String(Object obj) {
        if (obj == null)
            return "NULL";
        else {
            if (obj instanceof Blob) {
                Blob blob = (Blob) obj;
                return "Blob(" + blob.hashCode() + ")";
            } else if (obj instanceof Clob) {
                Clob clob = (Clob) obj;
                return "Clob(" + clob.hashCode() + ")";
            } else if (obj instanceof byte[] || obj instanceof char[]) {
                if (Array.getLength(obj) > 10240)
                    return "*BigData[len=" + Array.getLength(obj) + "]";
            } else if (obj instanceof InputStream) {
                try {
                    obj = "*InputStream[len=" + ((InputStream) obj).available() + "]";
                }
                catch (IOException e) {}
            } else if (obj instanceof Reader) {
                obj = "*Reader@" + obj.hashCode();
            }
            return Castors.me().castToString(obj); // TODO 太长的话,应该截取一部分
        }
    }
}
